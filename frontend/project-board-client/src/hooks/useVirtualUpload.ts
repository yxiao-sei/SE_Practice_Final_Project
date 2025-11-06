export const useVirtualUpload = ({ accept, multiple, end }: { accept: string, multiple: boolean, end: (files: File[]) => void }) => {
  const inputRef = document.createElement("input");
  inputRef.type = "file";
  inputRef.accept = accept;
  inputRef.multiple = multiple;

  const isAcceptFile = (file: File) => accept.includes(file.type);

  const clear = () => {
    inputRef.value = "";
    inputRef.onchange = null;
  };

  const handle = () => {
    inputRef.click();
    inputRef.onchange = (e: Event) => {
      const target = e.target as HTMLInputElement;
      const files = target.files;
      if (files) {
        end(Array.from(files));
      }
      clear();
    };
  };

  const handleDragEnter = (event: DragEvent) => {
    inputRef.focus();
  };

  const handleDragLeave = (event: DragEvent) => {
    inputRef.blur();
    if (event.currentTarget && event.currentTarget instanceof HTMLElement) {
      event.currentTarget.style.border = '';
    }
  };

  const handleDragOver = (event: DragEvent) => {
    event.preventDefault();
    event.stopPropagation();
    if (event.currentTarget && event.currentTarget instanceof HTMLElement) {
      event.currentTarget.style.border = '2px solid rgba(58,193,76,0.8)';
    }
  };

  const handleDrop = async (event: DragEvent) => {
    event.preventDefault();
    event.stopPropagation();
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      const docs: File[] = [];
      for (const file of files) {
        if (isAcceptFile(file)) docs.push(file)
      }
      end(docs);
    }
    if (event.currentTarget && event.currentTarget instanceof HTMLElement) {
      event.currentTarget.style.border = '';
      event.currentTarget.style.cursor = "auto";
    }
  };

  return {
    handle,
    handleDragEnter,
    handleDragLeave,
    handleDragOver,
    handleDrop
  };
};
