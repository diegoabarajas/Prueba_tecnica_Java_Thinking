<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="text-h5 text-weight-bold">Clientes</div>
        <div class="text-caption text-grey-6">Base de clientes registrados</div>
      </div>
      <q-btn
        v-if="authStore.isAdmin"
        color="primary"
        label="Nuevo Cliente"
        icon="add"
        unelevated
        @click="openFormDialog"
      />
    </div>

    <q-table
      :rows="clientes"
      :columns="columns"
      row-key="id"
      flat
      bordered
      :loading="loading"
      :filter="search"
      no-data-label="No hay clientes registrados"
    >
      <template #top-right>
        <q-input v-model="search" dense outlined placeholder="Buscar..." debounce="300">
          <template #append><q-icon name="search" /></template>
        </q-input>
      </template>

      <template #body-cell-acciones="props">
        <q-td :props="props" class="q-gutter-xs">
          <q-btn
            v-if="authStore.isAdmin"
            flat dense round icon="edit" color="primary" size="sm"
            @click="editCliente(props.row)"
          ><q-tooltip>Editar</q-tooltip></q-btn>
          <q-btn
            v-if="authStore.isAdmin"
            flat dense round icon="delete" color="negative" size="sm"
            @click="confirmDelete(props.row.id)"
          ><q-tooltip>Eliminar</q-tooltip></q-btn>
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="showDialog" @hide="resetForm" persistent>
      <q-card style="min-width: 420px">
        <q-card-section class="row items-center bg-primary text-white">
          <q-icon :name="editingId ? 'edit' : 'person_add'" size="sm" class="q-mr-sm" />
          <div class="text-h6">{{ editingId ? 'Editar' : 'Nuevo' }} Cliente</div>
          <q-space />
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-section>

        <q-card-section class="q-gutter-md q-pt-lg">
          <q-input
            v-model="form.nombre"
            label="Nombre *"
            outlined dense
            :rules="[val => !!val || 'El nombre es requerido']"
            lazy-rules
          />
          <q-input
            v-model="form.correo"
            label="Correo *"
            type="email"
            outlined dense
            :rules="[
              val => !!val || 'El correo es requerido',
              val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Correo inválido'
            ]"
            lazy-rules
          />
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn color="primary" label="Guardar" unelevated :loading="saving" @click="saveCliente" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useQuasar } from 'quasar'
import type { Cliente } from '@/types/models'
import { apiClient } from '@/utils/apiClient'

const authStore = useAuth()
const $q = useQuasar()

const clientes = ref<Cliente[]>([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingId = ref<number | null>(null)
const search = ref('')

const form = reactive({ nombre: '', correo: '' })

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left' as const, sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left' as const, sortable: true },
  { name: 'correo', label: 'Correo', field: 'correo', align: 'left' as const },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' as const },
]

onMounted(loadClientes)

async function loadClientes() {
  loading.value = true
  try {
    const response = await apiClient.getAxiosInstance().get<Cliente[]>('/api/clientes')
    clientes.value = response.data
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar clientes', position: 'top' })
  } finally {
    loading.value = false
  }
}

function openFormDialog() {
  resetForm()
  showDialog.value = true
}

function editCliente(cliente: Cliente) {
  editingId.value = cliente.id
  form.nombre = cliente.nombre
  form.correo = cliente.correo
  showDialog.value = true
}

async function saveCliente() {
  authStore.requireAdmin()
  if (!form.nombre || !form.correo) {
    $q.notify({ type: 'warning', message: 'Complete los campos obligatorios', position: 'top' })
    return
  }
  saving.value = true
  try {
    const axios = apiClient.getAxiosInstance()
    if (editingId.value) {
      await axios.put(`/api/clientes/${editingId.value}`, form)
    } else {
      await axios.post('/api/clientes', form)
    }
    $q.notify({
      type: 'positive',
      message: `Cliente ${editingId.value ? 'actualizado' : 'creado'} exitosamente`,
      position: 'top',
    })
    showDialog.value = false
    await loadClientes()
  } catch (err: unknown) {
    const axiosError = err as { response?: { data?: { message?: string } } }
    $q.notify({
      type: 'negative',
      message: axiosError.response?.data?.message || 'Error al guardar cliente',
      position: 'top',
    })
  } finally {
    saving.value = false
  }
}

function confirmDelete(id: number) {
  authStore.requireAdmin()
  $q.dialog({
    title: 'Eliminar cliente',
    message: '¿Confirmas que deseas eliminar este cliente?',
    cancel: { flat: true, label: 'Cancelar' },
    ok: { color: 'negative', label: 'Eliminar', unelevated: true },
    persistent: true,
  }).onOk(async () => {
    try {
      await apiClient.getAxiosInstance().delete(`/api/clientes/${id}`)
      $q.notify({ type: 'positive', message: 'Cliente eliminado', position: 'top' })
      await loadClientes()
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
  form.nombre = ''
  form.correo = ''
}
</script>
