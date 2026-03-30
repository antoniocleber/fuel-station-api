import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fuelPumpsApi } from '../api/fuelPumpsApi';
import { FuelPumpRequest } from '../types';
import { useUIStore } from '../stores/uiStore';

export const FUEL_PUMPS_KEY = ['fuel-pumps'];

export const useFuelPumps = () => {
  return useQuery({
    queryKey: FUEL_PUMPS_KEY,
    queryFn: fuelPumpsApi.getAll,
  });
};

export const useCreateFuelPump = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: (data: FuelPumpRequest) => fuelPumpsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUEL_PUMPS_KEY });
      addNotification('Bomba criada com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};

export const useUpdateFuelPump = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: FuelPumpRequest }) =>
      fuelPumpsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUEL_PUMPS_KEY });
      addNotification('Bomba atualizada com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};

export const useDeleteFuelPump = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: (id: number) => fuelPumpsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUEL_PUMPS_KEY });
      addNotification('Bomba removida com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};
