import {
  createVNode,
  render,
  type ComponentInstance,
  type Component,
} from "vue";

type ModalOptions = {
  header: string;
  subheader: string;
  successButtonText?: string;
  cancelButtonText?: string;
  appendTo?: string;
  [x: string]: any;
};

type ModalInstance = ComponentInstance<Component>;

const modalInstances = new Map<ModalInstance, () => void>();

const genContainer = () => {
  return document.createElement("div");
};

const getAppendToElement = (props: ModalOptions) => {
  let appendTo = document.body;
  if (props.appendTo) {
    appendTo = document.querySelector(props.appendTo)!;
  }
  return appendTo;
};
function initModalInstance(
  props: ModalOptions,
  container: any,
  ModalComponent: Component
): ModalInstance {
  const vnode = createVNode(ModalComponent, props);
  render(vnode, container);
  getAppendToElement(props).appendChild(container.firstElementChild);
  return vnode.component;
}

export function showModal(options: ModalOptions, ModalComponent: Component) {
  const container = genContainer();
  const onClose = () => {
    render(null, container);
    modalInstances.delete(vm);
  };
  const props = { ...options, visible: true, closeModal: onClose };
  const instance = initModalInstance(props, container, ModalComponent);
  const vm = instance.proxy;
  modalInstances.set(vm, onClose);
  return vm;
}

export function closeAllModals(): void {
  modalInstances.forEach((onClose) => onClose());
  modalInstances.clear();
}
