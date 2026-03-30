import React, { useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  OutlinedInput,
  FormHelperText,
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { FuelPump } from '../../types';
import { useFuelTypes } from '../../hooks/useFuelTypes';

const schema = z.object({
  name: z
    .string()
    .min(2, 'Nome deve ter no mínimo 2 caracteres')
    .max(100, 'Nome deve ter no máximo 100 caracteres'),
  fuelTypeIds: z.array(z.number()).min(1, 'Selecione pelo menos um tipo de combustível'),
});

type FormData = z.infer<typeof schema>;

interface FuelPumpFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: FormData) => void;
  loading?: boolean;
  editData?: FuelPump | null;
}

const FuelPumpFormModal: React.FC<FuelPumpFormModalProps> = ({
  open,
  onClose,
  onSubmit,
  loading = false,
  editData,
}) => {
  const { data: fuelTypes } = useFuelTypes();

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { name: '', fuelTypeIds: [] },
  });

  useEffect(() => {
    if (editData) {
      reset({
        name: editData.name,
        fuelTypeIds: editData.fuelTypes.map((ft) => ft.id),
      });
    } else {
      reset({ name: '', fuelTypeIds: [] });
    }
  }, [editData, reset, open]);

  const handleClose = () => {
    reset();
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>{editData ? 'Editar Bomba' : 'Nova Bomba'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(onSubmit)}>
        <DialogContent>
          <Box display="flex" flexDirection="column" gap={2} pt={1}>
            <Controller
              name="name"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Nome da Bomba"
                  placeholder="Ex: Bomba 01"
                  error={!!errors.name}
                  helperText={errors.name?.message}
                  fullWidth
                  required
                />
              )}
            />
            <Controller
              name="fuelTypeIds"
              control={control}
              render={({ field }) => (
                <FormControl fullWidth error={!!errors.fuelTypeIds} required>
                  <InputLabel>Tipos de Combustível</InputLabel>
                  <Select
                    {...field}
                    multiple
                    input={<OutlinedInput label="Tipos de Combustível" />}
                    renderValue={(selected) => (
                      <Box display="flex" flexWrap="wrap" gap={0.5}>
                        {(selected as number[]).map((id) => {
                          const ft = fuelTypes?.find((t) => t.id === id);
                          return ft ? <Chip key={id} label={ft.name} size="small" /> : null;
                        })}
                      </Box>
                    )}
                  >
                    {fuelTypes?.map((ft) => (
                      <MenuItem key={ft.id} value={ft.id}>
                        {ft.name}
                      </MenuItem>
                    ))}
                  </Select>
                  {errors.fuelTypeIds && (
                    <FormHelperText>{errors.fuelTypeIds.message}</FormHelperText>
                  )}
                </FormControl>
              )}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} disabled={loading}>
            Cancelar
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={loading}
            startIcon={loading ? <CircularProgress size={16} /> : null}
          >
            {loading ? 'Salvando...' : editData ? 'Atualizar' : 'Criar'}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
};

export default FuelPumpFormModal;
