<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="text-h5 text-weight-bold">Empresas</div>
        <div class="text-caption text-grey-6">Gestión de empresas registradas</div>
      </div>
      <q-btn
        v-if="authStore.isAdmin"
        color="primary"
        label="Nueva Empresa"
        icon="add"
        unelevated
        @click="openFormDialog()"
      />
    </div>

    <q-table
      :rows="empresas"
      :columns="columns"
      row-key="nit"
      flat
      bordered
      :loading="loading"
      :filter="search"
      no-data-label="No hay empresas registradas"
    >
      <template #top-right>
        <q-input v-model="search" dense outlined placeholder="Buscar..." debounce="300">
          <template #append>
            <q-icon name="search" />
          </template>
        </q-input>
      </template>

      <template #body-cell-acciones="props">
        <q-td :props="props" class="q-gutter-xs">
          <q-btn
            v-if="authStore.isAdmin"
            flat
            dense
            round
            icon="edit"
            color="primary"
            size="sm"
            @click="editEmpresa(props.row)"
          >
            <q-tooltip>Editar</q-tooltip>
          </q-btn>
          <q-btn
            v-if="authStore.isAdmin"
            flat
            dense
            round
            icon="delete"
            color="negative"
            size="sm"
            @click="confirmDelete(props.row.nit)"
          >
            <q-tooltip>Eliminar</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="showDialog" @hide="resetForm" persistent>
      <q-card style="min-width: 420px">
        <q-card-section class="row items-center bg-primary text-white">
          <q-icon :name="editingId ? 'edit' : 'add_business'" size="sm" class="q-mr-sm" />
          <div class="text-h6">{{ editingId ? 'Editar' : 'Nueva' }} Empresa</div>
          <q-space />
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-section>

        <q-card-section class="q-gutter-md q-pt-lg">
          <q-input
            v-model="form.nit"
            label="NIT *"
            outlined
            dense
            :disable="!!editingId"
            :rules="[val => !!val || 'El NIT es requerido']"
            lazy-rules
          />
          <q-input
            v-model="form.nombre"
            label="Nombre *"
            outlined
            dense
            :rules="[val => !!val || 'El nombre es requerido']"
            lazy-rules
          />
          <q-input
            v-model="form.direccion"
            label="Dirección"
            outlined
            dense
          />
          <q-input
            v-model="form.telefono"
            label="Teléfono"
            outlined
            dense
          />
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn
            color="primary"
            label="Guardar"
            unelevated
            :loading="saving"
            @click="saveEmpresa"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useQuasar } from 'quasar'
import type { Empresa } from '@/types/models'
import { apiClient } from '@/utils/apiClient'

const authStore = useAuth()
const $q = useQuasar()

const empresas = ref<Empresa[]>([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingId = ref<string | null>(null)
const search = ref('')

const form = reactive<Empresa>({
  nit: '',
  nombre: '',
  direccion: '',
  telefono: '',
})

const columns = [
  { name: 'nit', label: 'NIT', field: 'nit', align: 'left' as const, sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left' as const, sortable: true },
  { name: 'direccion', label: 'Dirección', field: 'direccion', align: 'left' as const },
  { name: 'telefono', label: 'Teléfono', field: 'telefono', align: 'left' as const },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' as const },
]

onMounted(loadEmpresas)

async function loadEmpresas() {
  loading.value = true
  try {
    const response = await apiClient.getAxiosInstance().get<Empresa[]>('/api/empresas')
    empresas.value = response.data
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar empresas', position: 'top' })
  } finally {
    loading.value = false
  }
}

function openFormDialog() {
  resetForm()
  showDialog.value = true
}

function editEmpresa(empresa: Empresa) {
  editingId.value = empresa.nit
  Object.assign(form, empresa)
  showDialog.value = true
}

async function saveEmpresa() {
  authStore.requireAdmin()
  if (!form.nit || !form.nombre) {
    $q.notify({ type: 'warning', message: 'Complete los campos obligatorios', position: 'top' })
    return
  }
  saving.value = true
  try {
    const axios = apiClient.getAxiosInstance()
    if (editingId.value) {
      await axios.put(`/api/empresas/${editingId.value}`, form)
    } else {
      await axios.post('/api/empresas', form)
    }
    $q.notify({
      type: 'positive',
      message: `Empresa ${editingId.value ? 'actualizada' : 'creada'} exitosamente`,
      position: 'top',
    })
    showDialog.value = false
    await loadEmpresas()
  } catch (err: unknown) {
    const axiosError = err as { response?: { data?: { message?: string } } }
    $q.notify({
      type: 'negative',
      message: axiosError.response?.data?.message || 'Error al guardar empresa',
      position: 'top',
    })
  } finally {
    saving.value = false
  }
}

function confirmDelete(nit: string) {
  authStore.requireAdmin()
  $q.dialog({
    title: 'Eliminar empresa',
    message: `¿Confirmas eliminar la empresa con NIT ${nit}?`,
    cancel: { flat: true, label: 'Cancelar' },
    ok: { color: 'negative', label: 'Eliminar', unelevated: true },
    persistent: true,
  }).onOk(async () => {
    try {
      await apiClient.getAxiosInstance().delete(`/api/empresas/${nit}`)
      $q.notify({ type: 'positive', message: 'Empresa eliminada', position: 'top' })
      await loadEmpresas()
    } catch (err: unknown) {
      const axiosError = err as { response?: { data?: { message?: string } } }
      $q.notify({
        type: 'negative',
        message: axiosError.response?.data?.message || 'Error al eliminar',
        position: 'top',
      })
    }
  })
}

function resetForm() {
  editingId.value = null
  Object.assign(form, { nit: '', nombre: '', direccion: '', telefono: '' })
}
</script>
