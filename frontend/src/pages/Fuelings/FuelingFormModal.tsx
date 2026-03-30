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
  FormHelperText,
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Fueling } from '../../types';
import { useFuelPumps } from '../../hooks/useFuelPumps';

const today = new Date().toISOString().split('T')[0];

const schema = z.object({
  pumpId: z
    .number({ invalid_type_error: 'Selecione uma bomba' })
    .positive('Selecione uma bomba'),
  fuelingDate: z
    .string()
    .min(1, 'Data é obrigatória')
    .refine((val) => val <= today, 'Data não pode ser no futuro'),
  liters: z
    .number({ invalid_type_error: 'Informe a quantidade em litros' })
    .positive('Litros deve ser maior que zero'),
  totalValue: z
    .number({ invalid_type_error: 'Informe o valor total' })
    .positive('Valor total deve ser maior que zero'),
});

type FormData = z.infer<typeof schema>;

interface FuelingFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: FormData) => void;
  loading?: boolean;
  editData?: Fueling | null;
}

const FuelingFormModal: React.FC<FuelingFormModalProps> = ({
  open,
  onClose,
  onSubmit,
  loading = false,
  editData,
}) => {
  const { data: fuelPumps } = useFuelPumps();

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { pumpId: 0, fuelingDate: today, liters: 0, totalValue: 0 },
  });

  useEffect(() => {
    if (editData) {
      reset({
        pumpId: editData.pump.id,
        fuelingDate: editData.fuelingDate,
        liters: editData.liters,
        totalValue: editData.totalValue,
      });
    } else {
      reset({ pumpId: 0, fuelingDate: today, liters: 0, totalValue: 0 });
    }
  }, [editData, reset, open]);

  const handleClose = () => {
    reset();
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>{editData ? 'Editar Abastecimento' : 'Novo Abastecimento'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(onSubmit)}>
        <DialogContent>
          <Box display="flex" flexDirection="column" gap={2} pt={1}>
            <Controller
              name="pumpId"
              control={control}
              render={({ field }) => (
                <FormControl fullWidth error={!!errors.pumpId} required>
                  <InputLabel>Bomba</InputLabel>
                  <Select
                    {...field}
                    label="Bomba"
                    onChange={(e) => field.onChange(Number(e.target.value))}
                  >
                    {fuelPumps?.map((pump) => (
                      <MenuItem key={pump.id} value={pump.id}>
                        {pump.name} ({pump.fuelTypes.map((ft) => ft.name).join(', ')})
                      </MenuItem>
                    ))}
                  </Select>
                  {errors.pumpId && (
                    <FormHelperText>{errors.pumpId.message}</FormHelperText>
                  )}
                </FormControl>
              )}
            />
            <Controller
              name="fuelingDate"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Data do Abastecimento"
                  type="date"
                  slotProps={{ inputLabel: { shrink: true }, htmlInput: { max: today } }}
                  error={!!errors.fuelingDate}
                  helperText={errors.fuelingDate?.message}
                  fullWidth
                  required
                />
              )}
            />
            <Controller
              name="liters"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  onChange={(e) => field.onChange(parseFloat(e.target.value) || 0)}
                  label="Quantidade (Litros)"
                  type="number"
                  inputProps={{ step: '0.001', min: '0' }}
                  placeholder="Ex: 40.000"
                  error={!!errors.liters}
                  helperText={errors.liters?.message}
                  fullWidth
                  required
                />
              )}
            />
            <Controller
              name="totalValue"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  onChange={(e) => field.onChange(parseFloat(e.target.value) || 0)}
                  label="Valor Total (R$)"
                  type="number"
                  inputProps={{ step: '0.01', min: '0' }}
                  placeholder="Ex: 235.60"
                  error={!!errors.totalValue}
                  helperText={errors.totalValue?.message}
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
            {loading ? 'Salvando...' : editData ? 'Atualizar' : 'Registrar'}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
};

export default FuelingFormModal;
