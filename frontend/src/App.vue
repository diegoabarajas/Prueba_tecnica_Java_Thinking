<template>
  <div id="app">
    <q-layout view="hHh lpR lpr">
      <q-header elevated class="bg-primary text-white">
        <q-toolbar>
          <q-btn
            v-if="authStore.isAuthenticated"
            flat
            dense
            round
            icon="menu"
            class="q-mr-sm"
            @click="drawer = !drawer"
          />
          <q-toolbar-title>
            <q-icon name="business" class="q-mr-sm" />
            Sistema de Gestión
          </q-toolbar-title>

          <div v-if="authStore.isAuthenticated" class="text-subtitle2 q-mr-md">
            {{ authStore.user?.email }}
            <q-chip
              :color="authStore.isAdmin ? 'warning' : 'positive'"
              text-color="white"
              dense
              class="q-ml-sm"
            >
              {{ authStore.isAdmin ? 'Admin' : 'Externo' }}
            </q-chip>
          </div>

          <q-btn
            v-if="authStore.isAuthenticated"
            flat
            dense
            round
            icon="logout"
            @click="handleLogout"
          >
            <q-tooltip>Cerrar sesión</q-tooltip>
          </q-btn>
        </q-toolbar>
      </q-header>

      <q-drawer v-if="authStore.isAuthenticated" v-model="drawer" side="left" bordered>
        <q-list padding>
          <q-item-label header>Navegación</q-item-label>

          <q-item
            v-for="item in navItems"
            :key="item.name"
            clickable
            :active="$route.name === item.name"
            active-class="text-primary bg-blue-1"
            :to="item.path"
            v-show="!item.adminOnly || authStore.isAdmin"
          >
            <q-item-section avatar>
              <q-icon :name="item.icon" />
            </q-item-section>
            <q-item-section>{{ item.label }}</q-item-section>
          </q-item>
        </q-list>
      </q-drawer>

      <q-page-container>
        <router-view />
      </q-page-container>
    </q-layout>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()
const drawer = ref(true)

const navItems = [
  { name: 'Dashboard', path: '/dashboard', icon: 'home', label: 'Dashboard', adminOnly: false },
  { name: 'Empresas', path: '/empresas', icon: 'store', label: 'Empresas', adminOnly: false },
  { name: 'Productos', path: '/productos', icon: 'shopping_cart', label: 'Productos', adminOnly: true },
  { name: 'Categorias', path: '/categorias', icon: 'label', label: 'Categorías', adminOnly: true },
  { name: 'Clientes', path: '/clientes', icon: 'people', label: 'Clientes', adminOnly: true },
  { name: 'Ordenes', path: '/ordenes', icon: 'receipt', label: 'Órdenes', adminOnly: true },
  { name: 'Inventario', path: '/inventario', icon: 'inventory', label: 'Inventario', adminOnly: true },
]

onMounted(async () => {
  await authStore.restoreSession()
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style>
#app {
  height: 100vh;
}
</style>
