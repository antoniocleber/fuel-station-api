import api from './axiosConfig';
import { Fueling, FuelingRequest, FuelingsFilters } from '../types';

export const fuelingsApi = {
  getAll: async (filters?: FuelingsFilters): Promise<Fueling[]> => {
    const params: Record<string, string | number> = {};
    if (filters?.pumpId) params.pumpId = filters.pumpId;
    if (filters?.startDate) params.startDate = filters.startDate;
    if (filters?.endDate) params.endDate = filters.endDate;
    const { data } = await api.get<Fueling[]>('/fuelings', { params });
    return data;
  },

  getById: async (id: number): Promise<Fueling> => {
    const { data } = await api.get<Fueling>(`/fuelings/${id}`);
    return data;
  },

  create: async (request: FuelingRequest): Promise<Fueling> => {
    const { data } = await api.post<Fueling>('/fuelings', request);
    return data;
  },

  update: async (id: number, request: FuelingRequest): Promise<Fueling> => {
    const { data } = await api.put<Fueling>(`/fuelings/${id}`, request);
    return data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/fuelings/${id}`);
  },
};
