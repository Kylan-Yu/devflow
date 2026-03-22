export interface AdminPageParams {
  page?: number;
  size?: number;
  search?: string;
  status?: string;
}

export interface AdminPage<T> {
  items: T[];
  currentPage: number;
  pageSize: number;
  totalPages: number;
  totalItems: number;
  hasNext: boolean;
  hasPrevious: boolean;
  search?: string;
}
