<template>
  <section class="empty-state">
    <div class="empty-glyph">{{ glyph }}</div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <slot />
  </section>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    glyph?: string
    title: string
    description: string
  }>(),
  {
    glyph: 'Ink'
  }
)
</script>
