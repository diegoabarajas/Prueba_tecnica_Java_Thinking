<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="text-h5 text-weight-bold">Inventario</div>
        <div class="text-caption text-grey-6">Productos por empresa</div>
      </div>
      <div class="row q-gutter-md items-center">
        <q-select
          v-model="filtroEmpresa"
          :options="empresasList"
          option-value="nit"
          option-label="nombre"
          label="Seleccionar empresa"
          outlined
          dense
          clearable
          emit-value
          map-options
          style="min-width: 260px"
          @update:model-value="loadInventario"
        />
        <q-btn
          color="primary"
          label="Descargar PDF"
          icon="picture_as_pdf"
          unelevated
          :disable="!filtroEmpresa"
          :loading="downloadingPdf"
          @click="downloadPDF"
        />
        <q-btn
          v-if="authStore.isAdmin"
          color="teal"
          label="Enviar por Email"
          icon="email"
          unelevated
          :disable="!filtroEmpresa"
          @click="showEmailDialog = true"
        />
      </div>
    </div>

    <q-banner
      v-if="!filtroEmpresa"
      class="bg-blue-1 text-blue-8 q-mb-lg rounded-borders"
      inline-actions
    >
      <template #avatar>
        <q-icon name="info" color="info" />
      </template>
      Selecciona una empresa para ver su inventario
    </q-banner>

    <q-table
      v-else
      :rows="inventario"
      :columns="columns"
      row-key="productoCodigo"
      flat
      bordered
      :loading="loading"
      no-data-label="No hay inventario para esta empresa"
    />

    <q-dialog v-model="showEmailDialog" persistent>
      <q-card style="min-width: 420px">
        <q-card-section class="row items-center bg-teal text-white">
          <q-icon name="email" size="sm" class="q-mr-sm" />
          <div class="text-h6">Enviar Inventario por Email</div>
          <q-space />
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-section>

        <q-card-section class="q-gutter-md q-pt-lg">
          <q-input
            v-model="emailForm.toEmail"
            label="Email destinatario *"
            type="email"
            outlined
            dense
            :rules="[
              val => !!val || 'El email es requerido',
              val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Email inválido'
            ]"
            lazy-rules
          />
          <q-input
            v-model="emailForm.subject"
            label="Asunto"
            outlined
            dense
          />
          <q-input
            v-model="emailForm.message"
            label="Mensaje"
            type="textarea"
            outlined
            dense
            autogrow
          />
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn
            color="teal"
            label="Enviar"
            unelevated
            icon="send"
            :loading="sendingEmail"
            @click="sendEmail"
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
import type { InventarioItem, Empresa } from '@/types/models'
import { apiClient } from '@/utils/apiClient'

const authStore = useAuth()
const $q = useQuasar()

const inventario = ref<InventarioItem[]>([])
const empresasList = ref<Empresa[]>([])
const filtroEmpresa = ref<string | null>(null)
const loading = ref(false)
const downloadingPdf = ref(false)
const sendingEmail = ref(false)
const showEmailDialog = ref(false)

const emailForm = reactive({
  toEmail: '',
  subject: 'Inventario de empresa',
  message: '',
})

const columns = [
  { name: 'empresaNit', label: 'Empresa NIT', field: 'empresaNit', align: 'left' as const },
  { name: 'productoCodigo', label: 'Código', field: 'productoCodigo', align: 'left' as const, sortable: true },
  { name: 'productoNombre', label: 'Producto', field: 'productoNombre', align: 'left' as const, sortable: true },
  { name: 'caracteristicas', label: 'Características', field: 'caracteristicas', align: 'left' as const },
]

onMounted(loadEmpresas)

async function loadEmpresas() {
  try {
    const response = await apiClient.getAxiosInstance().get<Empresa[]>('/api/empresas')
    empresasList.value = response.data
  } catch {
    // silencioso
  }
}

async function loadInventario() {
  if (!filtroEmpresa.value) {
    inventario.value = []
    return
  }
  loading.value = true
  try {
    const response = await apiClient.getAxiosInstance().get<InventarioItem[]>('/api/inventario', {
      params: { empresaNit: filtroEmpresa.value },
    })
    inventario.value = response.data
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar inventario', position: 'top' })
  } finally {
    loading.value = false
  }
}

async function downloadPDF() {
  if (!filtroEmpresa.value) return
  downloadingPdf.value = true
  try {
    const response = await apiClient.getAxiosInstance().get('/api/inventario/pdf', {
      params: { empresaNit: filtroEmpresa.value },
      responseType: 'blob',
    })
    const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }))
    const link = document.createElement('a')
    link.href = url
    link.download = `inventario-${filtroEmpresa.value}.pdf`
    document.body.appendChild(link)
    link.click()
    link.parentNode?.removeChild(link)
    window.URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: 'PDF descargado exitosamente', position: 'top' })
  } catch {
    $q.notify({ type: 'negative', message: 'Error al descargar el PDF', position: 'top' })
  } finally {
    downloadingPdf.value = false
  }
}

async function sendEmail() {
  authStore.requireAdmin()
  if (!emailForm.toEmail) {
    $q.notify({ type: 'warning', message: 'Ingresa un email destinatario', position: 'top' })
    return
  }
  sendingEmail.value = true
  try {
    await apiClient.getAxiosInstance().post('/api/inventario/email', {
      empresaNit: filtroEmpresa.value,
      toEmail: emailForm.toEmail,
      subject: emailForm.subject,
      message: emailForm.message,
    })
    $q.notify({ type: 'positive', message: 'Email enviado exitosamente', position: 'top' })
    showEmailDialog.value = false
    emailForm.toEmail = ''
    emailForm.message = ''
  } catch (err: unknown) {
    const axiosError = err as { response?: { data?: { message?: string } } }
    $q.notify({
      type: 'negative',
      message: axiosError.response?.data?.message || 'Error al enviar email',
      position: 'top',
    })
  } finally {
    sendingEmail.value = false
  }
}
</script>
