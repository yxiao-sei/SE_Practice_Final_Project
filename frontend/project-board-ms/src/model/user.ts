export interface DepartmentWithRoles {
  adminDepartmentName: string;
  departmentId: string;
  departmentName: string;
  performanceDepartmentCod: string;
  performanceDepartmentName: string;
  id: string;
  roles: string[];
}
export interface User {
  office: string;
  realName: string;
  roles: string;
  userType: string;
  username: string;
  departmentWithRoles: DepartmentWithRoles[];
  subDepartmentCode: string;
  subDepartmentName: string;
  departmentName: string;
  departmentId: string;
  avatar?: string;
}
