<template>
  <div>
    <div v-if="slots.length === 0" class="text-sm text-gray-500">
      Nenhum horário cadastrado.
    </div>
    <transition-group name="slot" tag="ul" class="space-y-2 mt-4">
      <li
        v-for="s in slots"
        :key="s.id"
        class="flex items-center justify-between p-2 md:p-3 bg-white rounded shadow-sm transform-gpu"
      >
        <div class="flex items-center gap-3">
          <div
            class="w-10 h-10 flex items-center justify-center rounded-md bg-gray-50 dark:bg-gray-800"
          >
            <svg class="w-6 h-6 text-gray-500" viewBox="0 0 24 24" fill="none">
              <path
                d="M8 7V3h8v4"
                stroke="currentColor"
                stroke-width="1.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <div>
            <div class="font-medium text-sm md:text-base">
              {{ formatInterval(s.start, s.end) }}
            </div>
            <div class="text-xs text-gray-500 mt-0.5">
              <span class="inline-block mr-2">Status:</span>
              <span :class="statusClass(s.status)">{{ s.status }}</span>
            </div>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <template v-if="patientMode">
            <button
              v-if="s.status === 'available'"
              @click="$emit('book', s)"
              class="inline-flex items-center justify-center px-2 py-1 md:px-3 md:py-1 bg-green-600 text-white rounded text-sm transition-transform duration-150 ease-out active:scale-95"
              :aria-label="`Agendar horário ${formatInterval(s.start, s.end)}`"
            >
              <svg class="w-4 h-4 mr-2" viewBox="0 0 24 24" fill="none">
                <path
                  d="M12 5v14M5 12h14"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              Agendar
            </button>
            <div v-else class="text-sm text-gray-500">Indisponível</div>
          </template>
          <template v-else>
            <Menu as="div" class="relative inline-block text-left">
              <MenuButton
                class="inline-flex justify-center w-full rounded-md border border-gray-200 bg-white px-2 py-1 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none"
                aria-label="Opções do horário"
                :aria-expanded="false"
              >
                <EllipsisVerticalIcon class="w-5 h-5 text-gray-600" />
              </MenuButton>

              <MenuItems
                class="absolute right-0 mt-2 w-40 origin-top-right bg-white divide-y divide-gray-100 rounded-md shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none z-10"
              >
                <div class="p-1">
                  <MenuItem v-slot="{ active }">
                    <button
                      @click="$emit('edit', s)"
                      :class="[
                        'group flex rounded-md items-center w-full px-2 py-2 text-sm',
                        active ? 'bg-gray-100 text-gray-900' : 'text-gray-700',
                      ]"
                    >
                      <PencilIcon class="w-4 h-4 mr-2 text-gray-500" />
                      Editar
                    </button>
                  </MenuItem>
                  <MenuItem v-slot="{ active }">
                    <button
                      @click="$emit('delete', s)"
                      :class="[
                        'group flex rounded-md items-center w-full px-2 py-2 text-sm',
                        active ? 'bg-gray-100 text-gray-900' : 'text-red-600',
                      ]"
                    >
                      <TrashIcon class="w-4 h-4 mr-2 text-red-500" />
                      Excluir
                    </button>
                  </MenuItem>
                </div>
              </MenuItems>
            </Menu>
          </template>
        </div>
      </li>
    </transition-group>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import { Menu, MenuButton, MenuItems, MenuItem } from "@headlessui/vue";
import {
  EllipsisVerticalIcon,
  PencilIcon,
  TrashIcon,
} from "@heroicons/vue/24/outline";

export default defineComponent({
  name: "SlotList",
  props: {
    slots: { type: Array as PropType<Array<any>>, default: () => [] },
    // quando true, mostra ação de "Agendar" em slots disponíveis
    patientMode: { type: Boolean as PropType<boolean>, default: false },
  },
  components: {
    Menu,
    MenuButton,
    MenuItems,
    MenuItem,
    EllipsisVerticalIcon,
    PencilIcon,
    TrashIcon,
  },
  methods: {
    formatInterval(start: string, end: string) {
      try {
        const s = new Date(start);
        const e = new Date(end);
        return `${s.toLocaleString()} — ${e.toLocaleString()}`;
      } catch (e) {
        return `${start} — ${end}`;
      }
    },
    statusClass(status: string) {
      const map: Record<string, string> = {
        available:
          "inline-block px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800",
        booked:
          "inline-block px-2 py-0.5 rounded text-xs font-medium bg-yellow-100 text-yellow-800",
        cancelled:
          "inline-block px-2 py-0.5 rounded text-xs font-medium bg-red-100 text-red-800",
      };
      return (
        map[status] ||
        "inline-block px-2 py-0.5 rounded text-xs font-medium bg-gray-100 text-gray-700"
      );
    },
  },
});
</script>

<style scoped></style>
