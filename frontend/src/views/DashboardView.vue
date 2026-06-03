<template>
  <q-page class="q-pa-lg">
    <div class="text-h4 q-mb-lg text-weight-bold">
      Bienvenido, {{ authStore.user?.email }}
    </div>
    <div class="text-subtitle1 text-grey-7 q-mb-xl">
      Selecciona un módulo para comenzar
    </div>

    <div class="row q-col-gutter-lg">
      <div
        v-for="card in dashboardCards"
        :key="card.name"
        class="col-xs-12 col-sm-6 col-md-4 col-lg-3"
        v-show="!card.adminOnly || authStore.isAdmin"
      >
        <q-card
          class="dashboard-card cursor-pointer"
          @click="$router.push(card.path)"
          flat
          bordered
        >
          <q-card-section class="text-center q-pa-xl">
            <q-icon :name="card.icon" :color="card.color" size="56px" />
            <div class="text-h6 q-mt-md">{{ card.label }}</div>
            <div class="text-caption text-grey-6 q-mt-xs">{{ card.description }}</div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()

const dashboardCards = [
  {
    name: 'empresas',
    path: '/empresas',
    icon: 'store',
    color: 'primary',
    label: 'Empresas',
    description: 'Gestiona las empresas registradas',
    adminOnly: false,
  },
  {
    name: 'productos',
    path: '/productos',
    icon: 'shopping_cart',
    color: 'secondary',
    label: 'Productos',
    description: 'Administra el catálogo de productos',
    adminOnly: true,
  },
  {
    name: 'categorias',
    path: '/categorias',
    icon: 'label',
    color: 'accent',
    label: 'Categorías',
    description: 'Organiza productos por categoría',
    adminOnly: true,
  },
  {
    name: 'clientes',
    path: '/clientes',
    icon: 'people',
    color: 'positive',
    label: 'Clientes',
    description: 'Gestiona la base de clientes',
    adminOnly: true,
  },
  {
    name: 'ordenes',
    path: '/ordenes',
    icon: 'receipt',
    color: 'info',
    label: 'Órdenes',
    description: 'Consulta el historial de órdenes',
    adminOnly: true,
  },
  {
    name: 'inventario',
    path: '/inventario',
    icon: 'inventory',
    color: 'warning',
    label: 'Inventario',
    description: 'Consulta stock y descarga reportes',
    adminOnly: true,
  },
]
</script>

<style scoped>
.dashboard-card {
  transition: all 0.25s ease;
}
.dashboard-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}
</style>
