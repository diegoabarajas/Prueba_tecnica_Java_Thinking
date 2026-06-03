<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="text-h5 text-weight-bold">Productos</div>
        <div class="text-caption text-grey-6">Catálogo de productos por empresa</div>
      </div>
      <div class="row q-gutter-md items-center">
        <q-select
          v-model="filtroEmpresa"
          :options="empresasList"
          option-value="nit"
          option-label="nombre"
          label="Filtrar por empresa"
          outlined
          dense
          clearable
          emit-value
          map-options
          style="min-width: 240px"
          @update:model-value="loadProductos"
        />
        <q-btn
          v-if="authStore.isAdmin"
          color="primary"
          label="Nuevo Producto"
          icon="add"
          unelevated
          @click="openFormDialog"
        />
      </div>
    </div>

    <q-table
      :rows="productos"
      :columns="columns"
      row-key="codigo"
      flat
      bordered
      :loading="loading"
      :filter="search"
      no-data-label="No hay productos registrados"
    >
      <template #top-right>
        <q-input v-model="search" dense outlined placeholder="Buscar..." debounce="300">
          <template #append><q-icon name="search" /></template>
        </q-input>
      </template>

      <template #body-cell-precios="props">
        <q-td :props="props">
          <q-chip
            v-for="p in props.row.precios"
            :key="p.moneda"
            dense
            color="blue-1"
            text-color="primary"
          >
            {{ p.moneda }} {{ p.precio.toLocaleString() }}
          </q-chip>
          <span v-if="!props.row.precios?.length" class="text-grey-5">—</span>
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="showDialog" @hide="resetForm" persistent>
      <q-card style="min-width: 500px">
        <q-card-section class="row items-center bg-primary text-white">
          <q-icon name="add_shopping_cart" size="sm" class="q-mr-sm" />
          <div class="text-h6">Nuevo Producto</div>
          <q-space />
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-section>

        <q-card-section class="q-gutter-md q-pt-lg">
          <q-input
            v-model="form.codigo"
            label="Código *"
            outlined dense
            :rules="[val => !!val || 'El código es requerido']"
            lazy-rules
          />
          <q-input
            v-model="form.nombre"
            label="Nombre *"
            outlined dense
            :rules="[val => !!val || 'El nombre es requerido']"
            lazy-rules
          />
          <q-input
            v-model="form.caracteristicas"
            label="Características"
            outlined dense
            type="textarea"
            autogrow
          />
          <q-select
            v-model="form.empresaNit"
            :options="empresasList"
            option-value="nit"
            option-label="nombre"
            label="Empresa *"
            outlined dense
            emit-value
            map-options
            :rules="[val => !!val || 'La empresa es requerida']"
            lazy-rules
          />

          <div>
            <div class="text-subtitle2 q-mb-sm">Precios</div>
            <div
              v-for="(precio, idx) in form.precios"
              :key="idx"
              class="row q-gutter-sm q-mb-sm items-center"
            >
              <q-select
                v-model="precio.moneda"
                :options="monedas"
                label="Moneda"
                outlined dense
                style="min-width: 110px"
              />
              <q-input
                v-model.number="precio.precio"
                label="Precio"
                outlined dense
                type="number"
                class="col"
              />
              <q-btn flat round dense icon="delete" color="negative" @click="removePrecio(idx)" />
            </div>
            <q-btn
              flat
              dense
              icon="add"
              label="Agregar precio"
              color="primary"
              @click="addPrecio"
            />
          </div>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn color="primary" label="Guardar" unelevated :loading="saving" @click="saveProducto" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useQuasar } from 'quasar'
import type { Producto, ProductoPrecio, Empresa } from '@/types/models'
import { apiClient } from '@/utils/apiClient'

const authStore = useAuth()
const $q = useQuasar()

const productos = ref<Producto[]>([])
const empresasList = ref<Empresa[]>([])
const filtroEmpresa = ref<string | null>(null)
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const search = ref('')
const monedas = ['COP', 'USD', 'EU']

const form = reactive<{ codigo: string; nombre: string; caracteristicas: string; empresaNit: string; precios: ProductoPrecio[] }>({
  codigo: '',
  nombre: '',
  caracteristicas: '',
  empresaNit: '',
  precios: [],
})

const columns = [
  { name: 'codigo', label: 'Código', field: 'codigo', align: 'left' as const, sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left' as const, sortable: true },
  { name: 'caracteristicas', label: 'Características', field: 'caracteristicas', align: 'left' as const },
  { name: 'empresaNit', label: 'Empresa NIT', field: 'empresaNit', align: 'left' as const },
  { name: 'precios', label: 'Precios', field: 'precios', align: 'left' as const },
]

onMounted(() => {
  loadEmpresas()
  loadProductos()
})

async function loadEmpresas() {
  try {
    const response = await apiClient.getAxiosInstance().get<Empresa[]>('/api/empresas')
    empresasList.value = response.data
  } catch {
    // silencioso
  }
}

async function loadProductos() {
  loading.value = true
  try {
    const params = filtroEmpresa.value ? { empresaNit: filtroEmpresa.value } : {}
    const response = await apiClient.getAxiosInstance().get<Producto[]>('/api/productos', { params })
    productos.value = response.data
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar productos', position: 'top' })
  } finally {
    loading.value = false
  }
}

function openFormDialog() {
  resetForm()
  showDialog.value = true
}

function addPrecio() {
  form.precios.push({ moneda: 'COP', precio: 0 })
}

function removePrecio(idx: number) {
  form.precios.splice(idx, 1)
}

async function saveProducto() {
  authStore.requireAdmin()
  if (!form.codigo || !form.nombre || !form.empresaNit) {
    $q.notify({ type: 'warning', message: 'Complete los campos obligatorios', position: 'top' })
    return
  }
  saving.value = true
  try {
    await apiClient.getAxiosInstance().post('/api/productos', { ...form })
    $q.notify({ type: 'positive', message: 'Producto creado exitosamente', position: 'top' })
    showDialog.value = false
    await loadProductos()
  } catch (err: unknown) {
    const axiosError = err as { response?: { data?: { message?: string } } }
    $q.notify({
      type: 'negative',
      message: axiosError.response?.data?.message || 'Error al guardar producto',
      position: 'top',
    })
  } finally {
    saving.value = false
  }
}

function resetForm() {
  form.codigo = ''
  form.nombre = ''
  form.caracteristicas = ''
  form.empresaNit = ''
  form.precios = []
}
</script>
