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
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { FuelType } from '../../types';

const schema = z.object({
  name: z
    .string()
    .min(2, 'Nome deve ter no mínimo 2 caracteres')
    .max(100, 'Nome deve ter no máximo 100 caracteres'),
  pricePerLiter: z
    .number({ invalid_type_error: 'Informe um valor válido' })
    .positive('Preço deve ser maior que zero'),
});

type FormData = z.infer<typeof schema>;

interface FuelTypeFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: FormData) => void;
  loading?: boolean;
  editData?: FuelType | null;
}

const FuelTypeFormModal: React.FC<FuelTypeFormModalProps> = ({
  open,
  onClose,
  onSubmit,
  loading = false,
  editData,
}) => {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { name: '', pricePerLiter: 0 },
  });

  useEffect(() => {
    if (editData) {
      reset({ name: editData.name, pricePerLiter: editData.pricePerLiter });
    } else {
      reset({ name: '', pricePerLiter: 0 });
    }
  }, [editData, reset, open]);

  const handleClose = () => {
    reset();
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        {editData ? 'Editar Tipo de Combustível' : 'Novo Tipo de Combustível'}
      </DialogTitle>
      <Box component="form" onSubmit={handleSubmit(onSubmit)}>
        <DialogContent>
          <Box display="flex" flexDirection="column" gap={2} pt={1}>
            <Controller
              name="name"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Nome"
                  placeholder="Ex: Gasolina Comum"
                  error={!!errors.name}
                  helperText={errors.name?.message}
                  fullWidth
                  required
                />
              )}
            />
            <Controller
              name="pricePerLiter"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  onChange={(e) => field.onChange(parseFloat(e.target.value) || 0)}
                  label="Preço por Litro (R$)"
                  type="number"
                  inputProps={{ step: '0.001', min: '0' }}
                  placeholder="Ex: 5.890"
                  error={!!errors.pricePerLiter}
                  helperText={errors.pricePerLiter?.message}
                  fullWidth
                  required
                />
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

export default FuelTypeFormModal;
