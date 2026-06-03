<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="text-h5 text-weight-bold">Categorías</div>
        <div class="text-caption text-grey-6">Organización de productos por categoría</div>
      </div>
      <q-btn
        v-if="authStore.isAdmin"
        color="primary"
        label="Nueva Categoría"
        icon="add"
        unelevated
        @click="openFormDialog"
      />
    </div>

    <q-table
      :rows="categorias"
      :columns="columns"
      row-key="id"
      flat
      bordered
      :loading="loading"
      :filter="search"
      no-data-label="No hay categorías registradas"
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
            @click="editCategoria(props.row)"
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
      <q-card style="min-width: 400px">
        <q-card-section class="row items-center bg-primary text-white">
          <q-icon :name="editingId ? 'edit' : 'add_circle'" size="sm" class="q-mr-sm" />
          <div class="text-h6">{{ editingId ? 'Editar' : 'Nueva' }} Categoría</div>
          <q-space />
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-lg">
          <q-input
            v-model="form.nombre"
            label="Nombre *"
            outlined
            dense
            autofocus
            :rules="[val => !!val || 'El nombre es requerido']"
            lazy-rules
          />
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn color="primary" label="Guardar" unelevated :loading="saving" @click="saveCategoria" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useQuasar } from 'quasar'
import type { Categoria } from '@/types/models'
import { apiClient } from '@/utils/apiClient'

const authStore = useAuth()
const $q = useQuasar()

const categorias = ref<Categoria[]>([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingId = ref<number | null>(null)
const search = ref('')

const form = reactive({ nombre: '' })

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left' as const, sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left' as const, sortable: true },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' as const },
]

onMounted(loadCategorias)

async function loadCategorias() {
  loading.value = true
  try {
    const response = await apiClient.getAxiosInstance().get<Categoria[]>('/api/categorias')
    categorias.value = response.data
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar categorías', position: 'top' })
  } finally {
    loading.value = false
  }
}

function openFormDialog() {
  resetForm()
  showDialog.value = true
}

function editCategoria(categoria: Categoria) {
  editingId.value = categoria.id
  form.nombre = categoria.nombre
  showDialog.value = true
}

async function saveCategoria() {
  authStore.requireAdmin()
  if (!form.nombre) {
    $q.notify({ type: 'warning', message: 'El nombre es requerido', position: 'top' })
    return
  }
  saving.value = true
  try {
    const axios = apiClient.getAxiosInstance()
    if (editingId.value) {
      await axios.put(`/api/categorias/${editingId.value}`, form)
    } else {
      await axios.post('/api/categorias', form)
    }
    $q.notify({
      type: 'positive',
      message: `Categoría ${editingId.value ? 'actualizada' : 'creada'} exitosamente`,
      position: 'top',
    })
    showDialog.value = false
    await loadCategorias()
  } catch (err: unknown) {
    const axiosError = err as { response?: { data?: { message?: string } } }
    $q.notify({
      type: 'negative',
      message: axiosError.response?.data?.message || 'Error al guardar categoría',
      position: 'top',
    })
  } finally {
    saving.value = false
  }
}

function confirmDelete(id: number) {
  authStore.requireAdmin()
  $q.dialog({
    title: 'Eliminar categoría',
    message: '¿Confirmas que deseas eliminar esta categoría?',
    cancel: { flat: true, label: 'Cancelar' },
    ok: { color: 'negative', label: 'Eliminar', unelevated: true },
    persistent: true,
  }).onOk(async () => {
    try {
      await apiClient.getAxiosInstance().delete(`/api/categorias/${id}`)
      $q.notify({ type: 'positive', message: 'Categoría eliminada', position: 'top' })
      await loadCategorias()
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
}
</script>
