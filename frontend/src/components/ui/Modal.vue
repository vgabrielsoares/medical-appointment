<template>
  <TransitionRoot :show="modelValue" as="template">
    <Dialog
      as="div"
      class="fixed inset-0 z-40 overflow-y-auto"
      @close="$emit('update:modelValue', false)"
    >
      <div class="min-h-screen px-4 text-center">
        <TransitionChild
          as="template"
          enter="ease-out duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="ease-in duration-200"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <DialogOverlay class="fixed inset-0 bg-black bg-opacity-30" />
        </TransitionChild>

        <span class="inline-block h-screen align-middle" aria-hidden="true"
          >&#8203;</span
        >

        <TransitionChild
          as="template"
          enter="ease-out duration-300"
          enter-from="opacity-0 scale-95"
          enter-to="opacity-100 scale-100"
          leave="ease-in duration-200"
          leave-from="opacity-100 scale-100"
          leave-to="opacity-0 scale-95"
        >
          <div
            class="inline-block w-full max-w-lg p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-card shadow-xl rounded-md"
          >
            <DialogTitle class="text-lg font-medium text-gray-900"
              ><slot name="title"
            /></DialogTitle>
            <div class="mt-2"><slot name="body" /></div>
            <div class="mt-4 flex justify-end gap-2">
              <slot name="footer" />
            </div>
          </div>
        </TransitionChild>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import {
  Dialog,
  DialogOverlay,
  DialogTitle,
  TransitionChild,
  TransitionRoot,
} from "@headlessui/vue";

export default defineComponent({
  name: "UiModal",
  components: {
    Dialog,
    DialogOverlay,
    DialogTitle,
    TransitionChild,
    TransitionRoot,
  },
  props: { modelValue: { type: Boolean, required: true } },
  emits: ["update:modelValue"],
});
</script>

<style scoped></style>
