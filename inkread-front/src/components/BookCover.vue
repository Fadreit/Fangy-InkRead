<template>
  <div class="book-cover" :class="{ empty: !src }">
    <img v-if="src" :src="src" :alt="title" />
    <div v-else class="book-cover-fallback">
      <span>{{ title.slice(0, 2) || '书' }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  src?: string
  title: string
}>()
</script>
