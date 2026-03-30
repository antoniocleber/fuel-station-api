import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fuelingsApi } from '../api/fuelingsApi';
import { FuelingRequest, FuelingsFilters } from '../types';
import { useUIStore } from '../stores/uiStore';

export const FUELINGS_KEY = ['fuelings'];

export const useFuelings = (filters?: FuelingsFilters) => {
  return useQuery({
    queryKey: [...FUELINGS_KEY, filters],
    queryFn: () => fuelingsApi.getAll(filters),
  });
};

export const useCreateFueling = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: (data: FuelingRequest) => fuelingsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUELINGS_KEY });
      addNotification('Abastecimento registrado com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};

export const useUpdateFueling = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: FuelingRequest }) =>
      fuelingsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUELINGS_KEY });
      addNotification('Abastecimento atualizado com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};

export const useDeleteFueling = () => {
  const queryClient = useQueryClient();
  const addNotification = useUIStore((s) => s.addNotification);

  return useMutation({
    mutationFn: (id: number) => fuelingsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: FUELINGS_KEY });
      addNotification('Abastecimento removido com sucesso!', 'success');
    },
    onError: (error: Error) => {
      addNotification(error.message, 'error');
    },
  });
};
