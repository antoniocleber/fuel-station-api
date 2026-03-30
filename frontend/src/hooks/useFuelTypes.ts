import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fuelTypesApi } from '../api/fuelTypesApi';
import { FuelTypeRequest } from '../types';
import { useUIStore } from '../stores/uiStore';

export const FUEL_TYPES_KEY = ['fuel-types'];

export const useFuelTypes = () => {
  return useQuery({
    queryKey: FUEL_TYPES_KEY,
    queryFn: fuelTypesApi.getAll,
  });
};

export const useCreateFuelType = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: (data: FuelTypeRequest) => fuelTypesApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUEL_TYPES_KEY });
      addNotification('Tipo de combustível criado com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};

export const useUpdateFuelType = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: FuelTypeRequest }) =>
      fuelTypesApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUEL_TYPES_KEY });
      addNotification('Tipo de combustível atualizado com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};

export const useDeleteFuelType = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: (id: number) => fuelTypesApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUEL_TYPES_KEY });
      addNotification('Tipo de combustível removido com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};
