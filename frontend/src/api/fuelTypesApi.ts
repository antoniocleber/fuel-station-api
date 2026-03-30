import api from './axiosConfig';
import { FuelType, FuelTypeRequest } from '../types';

export const fuelTypesApi = {
  getAll: async (): Promise<FuelType[]> => {
    const { data } = await api.get<FuelType[]>('/fuel-types');
    return data;
  },

  getById: async (id: number): Promise<FuelType> => {
    const { data } = await api.get<FuelType>(`/fuel-types/${id}`);
    return data;
  },

  create: async (request: FuelTypeRequest): Promise<FuelType> => {
    const { data } = await api.post<FuelType>('/fuel-types', request);
    return data;
  },

  update: async (id: number, request: FuelTypeRequest): Promise<FuelType> => {
    const { data } = await api.put<FuelType>(`/fuel-types/${id}`, request);
    return data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/fuel-types/${id}`);
  },
};
