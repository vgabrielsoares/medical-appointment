declare module "*.vue" {
  import { DefineComponent } from "vue";
  const component: DefineComponent<{}, {}, any>;
  export default component;
  // permitir named exports usados por alguns SFCs (ex: pushToast)
  export const pushToast: any;
}
