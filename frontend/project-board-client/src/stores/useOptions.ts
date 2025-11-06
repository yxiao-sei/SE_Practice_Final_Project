import { defineStore } from "pinia";
import { ref } from "vue";

export const useOptions = defineStore('base-options', () => {
  const teacherRoles = ref<string[]>([]);
  const userTypeList = ref<any[]>([]);
  const setTeacherRoles = (payload: any[]) => {
    teacherRoles.value = payload;
  };
  const setUserTypeList = (payload: any[]) => {
    userTypeList.value = payload;
  };
  return {
    teacherRoles,
    setTeacherRoles,
    userTypeList,
    setUserTypeList
  };
});
