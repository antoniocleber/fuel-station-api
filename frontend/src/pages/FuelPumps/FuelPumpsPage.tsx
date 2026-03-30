import React, { useState } from 'react';
import { Box, Typography, Button, Paper, Chip } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { DataGrid, GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import {
  useFuelPumps,
  useCreateFuelPump,
  useUpdateFuelPump,
  useDeleteFuelPump,
} from '../../hooks/useFuelPumps';
import { FuelPump, FuelPumpRequest } from '../../types';
import FuelPumpFormModal from './FuelPumpFormModal';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import ErrorAlert from '../../components/common/ErrorAlert';
import { formatDateTime } from '../../utils/formatters';

const FuelPumpsPage: React.FC = () => {
  const { data: fuelPumps, isLoading, error, refetch } = useFuelPumps();
  const createMutation = useCreateFuelPump();
  const updateMutation = useUpdateFuelPump();
  const deleteMutation = useDeleteFuelPump();

  const [modalOpen, setModalOpen] = useState(false);
  const [editItem, setEditItem] = useState<FuelPump | null>(null);
  const [deleteItem, setDeleteItem] = useState<FuelPump | null>(null);

  const handleCreate = () => {
    setEditItem(null);
    setModalOpen(true);
  };

  const handleEdit = (item: FuelPump) => {
    setEditItem(item);
    setModalOpen(true);
  };

  const handleDelete = (item: FuelPump) => {
    setDeleteItem(item);
  };

  const handleFormSubmit = async (data: FuelPumpRequest) => {
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

  const columns: GridColDef[] = [
    { field: 'id', headerName: 'ID', width: 70 },
    { field: 'name', headerName: 'Nome', width: 150 },
    {
      field: 'fuelTypes',
      headerName: 'Combustíveis',
      flex: 1,
      minWidth: 200,
      sortable: false,
      renderCell: (params) => (
        <Box display="flex" flexWrap="wrap" gap={0.5} py={0.5}>
          {(params.value as { id: number; name: string }[]).map((ft) => (
            <Chip key={ft.id} label={ft.name} size="small" color="primary" variant="outlined" />
          ))}
        </Box>
      ),
    },
    {
      field: 'updatedAt',
      headerName: 'Atualizado em',
      width: 180,
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
          onClick={() => handleEdit(params.row as FuelPump)}
        />,
        <GridActionsCellItem
          key="delete"
          icon={<DeleteIcon />}
          label="Excluir"
          onClick={() => handleDelete(params.row as FuelPump)}
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
            Bombas de Combustível
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Gerencie as bombas de abastecimento do posto
          </Typography>
        </Box>
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate} size="large">
          Nova Bomba
        </Button>
      </Box>

      <Paper sx={{ height: 500, width: '100%' }}>
        <DataGrid
          rows={fuelPumps ?? []}
          columns={columns}
          loading={isLoading}
          pageSizeOptions={[10, 25, 50]}
          initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
          disableRowSelectionOnClick
          getRowHeight={() => 'auto'}
          sx={{ border: 'none', '& .MuiDataGrid-cell': { py: 1 } }}
        />
      </Paper>

      <FuelPumpFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleFormSubmit}
        loading={createMutation.isPending || updateMutation.isPending}
        editData={editItem}
      />

      <ConfirmDialog
        open={!!deleteItem}
        title="Confirmar Exclusão"
        message={`Tem certeza que deseja excluir a bomba "${deleteItem?.name}"? Esta ação não pode ser desfeita.`}
        onConfirm={handleConfirmDelete}
        onCancel={() => setDeleteItem(null)}
        confirmLabel="Excluir"
        loading={deleteMutation.isPending}
      />
    </Box>
  );
};

export default FuelPumpsPage;
