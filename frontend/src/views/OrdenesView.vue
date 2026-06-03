<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="text-h5 text-weight-bold">Órdenes</div>
        <div class="text-caption text-grey-6">Historial de órdenes (solo lectura)</div>
      </div>
    </div>

    <q-table
      :rows="ordenes"
      :columns="columns"
      row-key="id"
      flat
      bordered
      :loading="loading"
      :filter="search"
      no-data-label="No hay órdenes registradas"
    >
      <template #top-right>
        <q-input v-model="search" dense outlined placeholder="Buscar..." debounce="300">
          <template #append><q-icon name="search" /></template>
        </q-input>
      </template>

      <template #body-cell-fechaCreacion="props">
        <q-td :props="props">
          {{ formatFecha(props.row.fechaCreacion) }}
        </q-td>
      </template>

      <template #body-cell-items="props">
        <q-td :props="props">
          {{ props.row.items?.length ?? 0 }} producto(s)
        </q-td>
      </template>

      <template #body-cell-acciones="props">
        <q-td :props="props">
          <q-btn
            flat dense round icon="visibility" color="info" size="sm"
            @click="viewOrden(props.row)"
          ><q-tooltip>Ver detalle</q-tooltip></q-btn>
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="showDetail">
      <q-card style="min-width: 520px">
        <q-card-section class="row items-center bg-info text-white">
          <q-icon name="receipt_long" size="sm" class="q-mr-sm" />
          <div class="text-h6">Orden #{{ selectedOrden?.id }}</div>
          <q-space />
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-section>

        <q-card-section v-if="selectedOrden" class="q-pt-lg">
          <div class="row q-col-gutter-md q-mb-lg">
            <div class="col-6">
              <div class="text-caption text-grey-6">Cliente ID</div>
              <div class="text-body1 text-weight-medium">{{ selectedOrden.clienteId }}</div>
            </div>
            <div class="col-6">
              <div class="text-caption text-grey-6">Fecha</div>
              <div class="text-body1 text-weight-medium">{{ formatFecha(selectedOrden.fechaCreacion) }}</div>
            </div>
          </div>

          <q-separator class="q-mb-md" />
          <div class="text-subtitle2 q-mb-sm">Productos en la orden</div>

          <q-list bordered separator rounded>
            <q-item v-for="item in selectedOrden.items" :key="item.productoCodigo">
              <q-item-section avatar>
                <q-icon name="shopping_cart" color="primary" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ item.productoCodigo }}</q-item-label>
                <q-item-label caption>Cantidad: {{ item.cantidad }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item v-if="!selectedOrden.items?.length">
              <q-item-section class="text-grey-5 text-center">Sin productos</q-item-section>
            </q-item>
          </q-list>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cerrar" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Orden } from '@/types/models'
import { apiClient } from '@/utils/apiClient'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const ordenes = ref<Orden[]>([])
const loading = ref(false)
const showDetail = ref(false)
const selectedOrden = ref<Orden | null>(null)
const search = ref('')

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left' as const, sortable: true },
  { name: 'clienteId', label: 'Cliente ID', field: 'clienteId', align: 'left' as const },
  { name: 'fechaCreacion', label: 'Fecha', field: 'fechaCreacion', align: 'left' as const, sortable: true },
  { name: 'items', label: 'Ítems', field: 'items', align: 'left' as const },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' as const },
]

onMounted(loadOrdenes)

async function loadOrdenes() {
  loading.value = true
  try {
    const response = await apiClient.getAxiosInstance().get<Orden[]>('/api/ordenes')
    ordenes.value = response.data
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar órdenes', position: 'top' })
  } finally {
    loading.value = false
  }
}

function viewOrden(orden: Orden) {
  selectedOrden.value = orden
  showDetail.value = true
}

function formatFecha(fecha: string): string {
  if (!fecha) return '—'
  return new Date(fecha).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' })
}
</script>
