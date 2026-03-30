import api from './axiosConfig';
import { FuelPump, FuelPumpRequest } from '../types';

export const fuelPumpsApi = {
  getAll: async (): Promise<FuelPump[]> => {
    const { data } = await api.get<FuelPump[]>('/fuel-pumps');
    return data;
  },

  getById: async (id: number): Promise<FuelPump> => {
    const { data } = await api.get<FuelPump>(`/fuel-pumps/${id}`);
    return data;
  },

  create: async (request: FuelPumpRequest): Promise<FuelPump> => {
    const { data } = await api.post<FuelPump>('/fuel-pumps', request);
    return data;
  },

  update: async (id: number, request: FuelPumpRequest): Promise<FuelPump> => {
    const { data } = await api.put<FuelPump>(`/fuel-pumps/${id}`, request);
    return data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/fuel-pumps/${id}`);
  },
};
