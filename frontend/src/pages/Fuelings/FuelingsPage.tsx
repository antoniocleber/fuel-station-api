import React, { useState } from 'react';
import {
  Box,
  Typography,
  Button,
  Paper,
  Chip,
  Grid,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  IconButton,
  Tooltip,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import FilterAltOffIcon from '@mui/icons-material/FilterAltOff';
import { DataGrid, GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import {
  useFuelings,
  useCreateFueling,
  useUpdateFueling,
  useDeleteFueling,
} from '../../hooks/useFuelings';
import { useFuelPumps } from '../../hooks/useFuelPumps';
import { Fueling, FuelingRequest, FuelingsFilters } from '../../types';
import FuelingFormModal from './FuelingFormModal';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import ErrorAlert from '../../components/common/ErrorAlert';
import { formatCurrency, formatDate, formatLiters, formatDateTime } from '../../utils/formatters';

const FuelingsPage: React.FC = () => {
  const [filters, setFilters] = useState<FuelingsFilters>({});
  const { data: fuelings, isLoading, error, refetch } = useFuelings(filters);
  const { data: fuelPumps } = useFuelPumps();
  const createMutation = useCreateFueling();
  const updateMutation = useUpdateFueling();
  const deleteMutation = useDeleteFueling();

  const [modalOpen, setModalOpen] = useState(false);
  const [editItem, setEditItem] = useState<Fueling | null>(null);
  const [deleteItem, setDeleteItem] = useState<Fueling | null>(null);

  const handleCreate = () => {
    setEditItem(null);
    setModalOpen(true);
  };

  const handleEdit = (item: Fueling) => {
    setEditItem(item);
    setModalOpen(true);
  };

  const handleDelete = (item: Fueling) => {
    setDeleteItem(item);
  };

  const handleFormSubmit = async (data: FuelingRequest) => {
    if (editItem) {
      await updateMutation.mutateAsync({ id: editItem.id, data });
    } else {
      await createMutation.mutateAsync(data);
    }
    setModalOpen(false);
  };

  const handleConfirmDelete = async () => {
    if (deleteItem) {
      await deleteMutation.mutateAsync(deleteItem.id);
      setDeleteItem(null);
    }
  };

  const clearFilters = () => setFilters({});
  const hasFilters = Object.values(filters).some(Boolean);

  const columns: GridColDef[] = [
    { field: 'id', headerName: 'ID', width: 70 },
    {
      field: 'fuelingDate',
      headerName: 'Data',
      width: 120,
      valueFormatter: (value: string) => formatDate(value),
    },
    {
      field: 'pump',
      headerName: 'Bomba',
      width: 130,
      valueGetter: (_value: unknown, row: Fueling) => row.pump?.name ?? '-',
    },
    {
      field: 'fuelTypes',
      headerName: 'Combustíveis',
      flex: 1,
      minWidth: 180,
      sortable: false,
      valueGetter: (_value: unknown, row: Fueling) => row.pump?.fuelTypes ?? [],
      renderCell: (params) => (
        <Box display="flex" flexWrap="wrap" gap={0.5} py={0.5}>
          {(params.value as { id: number; name: string }[]).map((ft) => (
            <Chip key={ft.id} label={ft.name} size="small" variant="outlined" />
          ))}
        </Box>
      ),
    },
    {
      field: 'liters',
      headerName: 'Litros',
      width: 120,
      valueFormatter: (value: number) => formatLiters(value),
    },
    {
      field: 'totalValue',
      headerName: 'Valor Total',
      width: 130,
      renderCell: (params) => (
        <Chip
          label={formatCurrency(params.value as number)}
          color="success"
          variant="outlined"
          size="small"
        />
      ),
    },
    {
      field: 'updatedAt',
      headerName: 'Atualizado em',
      width: 160,
      valueFormatter: (value: string) => formatDateTime(value),
    },
    {
      field: 'actions',
      type: 'actions',
      headerName: 'Ações',
      width: 100,
      getActions: (params) => [
        <GridActionsCellItem
          key="edit"
          icon={<EditIcon />}
          label="Editar"
          onClick={() => handleEdit(params.row as Fueling)}
        />,
        <GridActionsCellItem
          key="delete"
          icon={<DeleteIcon />}
          label="Excluir"
          onClick={() => handleDelete(params.row as Fueling)}
          showInMenu
        />,
      ],
    },
  ];

  if (error) {
    return <ErrorAlert message={(error as Error).message} onRetry={refetch} />;
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box>
          <Typography variant="h4" gutterBottom>
            Abastecimentos
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Registre e gerencie os abastecimentos do posto
          </Typography>
        </Box>
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate} size="large">
          Registrar Abastecimento
        </Button>
      </Box>

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={4} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Filtrar por Bomba</InputLabel>
              <Select
                value={filters.pumpId ?? ''}
                label="Filtrar por Bomba"
                onChange={(e) =>
                  setFilters((f) => ({
                    ...f,
                    pumpId: e.target.value ? Number(e.target.value) : undefined,
                  }))
                }
              >
                <MenuItem value="">Todas as bombas</MenuItem>
                {fuelPumps?.map((pump) => (
                  <MenuItem key={pump.id} value={pump.id}>
                    {pump.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={4} md={3}>
            <TextField
              fullWidth
              size="small"
              label="Data Inicial"
              type="date"
              value={filters.startDate ?? ''}
              onChange={(e) =>
                setFilters((f) => ({ ...f, startDate: e.target.value || undefined }))
              }
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Grid>
          <Grid item xs={12} sm={4} md={3}>
            <TextField
              fullWidth
              size="small"
              label="Data Final"
              type="date"
              value={filters.endDate ?? ''}
              onChange={(e) =>
                setFilters((f) => ({ ...f, endDate: e.target.value || undefined }))
              }
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Grid>
          <Grid item xs={12} sm={12} md={3}>
            <Tooltip title="Limpar filtros">
              <span>
                <IconButton onClick={clearFilters} disabled={!hasFilters} color="primary">
                  <FilterAltOffIcon />
                </IconButton>
              </span>
            </Tooltip>
            {hasFilters && (
              <Chip
                label={`${fuelings?.length ?? 0} resultado(s)`}
                color="primary"
                size="small"
                sx={{ ml: 1 }}
              />
            )}
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{ height: 500, width: '100%' }}>
        <DataGrid
          rows={fuelings ?? []}
          columns={columns}
          loading={isLoading}
          pageSizeOptions={[10, 25, 50]}
          initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
          disableRowSelectionOnClick
          getRowHeight={() => 'auto'}
          sx={{ border: 'none', '& .MuiDataGrid-cell': { py: 1 } }}
        />
      </Paper>

      <FuelingFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleFormSubmit}
        loading={createMutation.isPending || updateMutation.isPending}
        editData={editItem}
      />

      <ConfirmDialog
        open={!!deleteItem}
        title="Confirmar Exclusão"
        message={`Tem certeza que deseja excluir o abastecimento de ${deleteItem ? formatLiters(deleteItem.liters) : ''} na ${deleteItem?.pump?.name}?`}
        onConfirm={handleConfirmDelete}
        onCancel={() => setDeleteItem(null)}
        confirmLabel="Excluir"
        loading={deleteMutation.isPending}
      />
    </Box>
  );
};

export default FuelingsPage;
