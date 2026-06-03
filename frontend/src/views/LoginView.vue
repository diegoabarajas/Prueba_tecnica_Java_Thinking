<template>
  <q-page class="flex flex-center login-background">
    <q-card style="width: 100%; max-width: 480px" class="q-pa-lg shadow-10">
      <q-card-section class="text-center q-pb-none">
        <q-icon name="business" size="56px" color="primary" />
        <div class="text-h5 q-mt-sm text-weight-bold">Sistema de Gestión</div>
        <div class="text-subtitle2 text-grey-6">Inicia sesión para continuar</div>
      </q-card-section>

      <q-card-section>
        <q-form @submit.prevent="handleLogin" class="q-gutter-md">
          <q-input
            v-model="form.email"
            label="Correo electrónico"
            type="email"
            outlined
            :rules="emailRules"
            lazy-rules
            prepend-icon="email"
          >
            <template #prepend>
              <q-icon name="email" />
            </template>
          </q-input>

          <q-input
            v-model="form.password"
            label="Contraseña"
            :type="showPassword ? 'text' : 'password'"
            outlined
            :rules="[val => !!val || 'La contraseña es requerida']"
            lazy-rules
          >
            <template #prepend>
              <q-icon name="lock" />
            </template>
            <template #append>
              <q-icon
                :name="showPassword ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="showPassword = !showPassword"
              />
            </template>
          </q-input>

          <q-banner v-if="authStore.error" class="bg-negative text-white rounded-borders">
            <template #avatar>
              <q-icon name="error" />
            </template>
            {{ authStore.error }}
          </q-banner>

          <q-btn
            type="submit"
            color="primary"
            label="Iniciar Sesión"
            size="lg"
            class="full-width"
            :loading="authStore.loading"
            :disable="authStore.loading"
            unelevated
          />
        </q-form>
      </q-card-section>

      <q-separator />

      <q-card-section class="text-caption text-center text-grey-6">
        <div class="text-weight-bold q-mb-xs">Credenciales de prueba</div>
        <div>Admin: admin@local.test / ChangeMe123!</div>
        <div>Externo: externo@local.test / ChangeMe123!</div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()
const showPassword = ref(false)

const form = reactive({
  email: '',
  password: '',
})

const emailRules = [
  (val: string) => !!val || 'El correo es requerido',
  (val: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Email inválido',
]

async function handleLogin() {
  const success = await authStore.login(form.email, form.password)
  if (success) {
    router.push('/dashboard')
  }
}
</script>

<style scoped>
.login-background {
  background: linear-gradient(135deg, #e3f0fb 0%, #e8f5f3 100%);
  min-height: 100vh;
}
</style>
