import React, { useState } from 'react';
import { Box, Typography, Button, Paper, Chip } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { DataGrid, GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import {
  useFuelTypes,
  useCreateFuelType,
  useUpdateFuelType,
  useDeleteFuelType,
} from '../../hooks/useFuelTypes';
import { FuelType, FuelTypeRequest } from '../../types';
import FuelTypeFormModal from './FuelTypeFormModal';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import ErrorAlert from '../../components/common/ErrorAlert';
import { formatCurrency, formatDateTime } from '../../utils/formatters';

const FuelTypesPage: React.FC = () => {
  const { data: fuelTypes, isLoading, error, refetch } = useFuelTypes();
  const createMutation = useCreateFuelType();
  const updateMutation = useUpdateFuelType();
  const deleteMutation = useDeleteFuelType();

  const [modalOpen, setModalOpen] = useState(false);
  const [editItem, setEditItem] = useState<FuelType | null>(null);
  const [deleteItem, setDeleteItem] = useState<FuelType | null>(null);

  const handleCreate = () => {
    setEditItem(null);
    setModalOpen(true);
  };

  const handleEdit = (item: FuelType) => {
    setEditItem(item);
    setModalOpen(true);
  };

  const handleDelete = (item: FuelType) => {
    setDeleteItem(item);
  };

  const handleFormSubmit = async (data: FuelTypeRequest) => {
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
    { field: 'name', headerName: 'Nome', flex: 1, minWidth: 150 },
    {
      field: 'pricePerLiter',
      headerName: 'Preço por Litro',
      width: 160,
      renderCell: (params) => (
        <Chip
          label={formatCurrency(params.value as number)}
          color="primary"
          variant="outlined"
          size="small"
        />
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
          onClick={() => handleEdit(params.row as FuelType)}
        />,
        <GridActionsCellItem
          key="delete"
          icon={<DeleteIcon />}
          label="Excluir"
          onClick={() => handleDelete(params.row as FuelType)}
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
            Tipos de Combustível
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Gerencie os tipos de combustível disponíveis no posto
          </Typography>
        </Box>
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate} size="large">
          Novo Tipo
        </Button>
      </Box>

      <Paper sx={{ height: 500, width: '100%' }}>
        <DataGrid
          rows={fuelTypes ?? []}
          columns={columns}
          loading={isLoading}
          pageSizeOptions={[10, 25, 50]}
          initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
          disableRowSelectionOnClick
          sx={{ border: 'none' }}
        />
      </Paper>

      <FuelTypeFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleFormSubmit}
        loading={createMutation.isPending || updateMutation.isPending}
        editData={editItem}
      />

      <ConfirmDialog
        open={!!deleteItem}
        title="Confirmar Exclusão"
        message={`Tem certeza que deseja excluir o tipo de combustível "${deleteItem?.name}"? Esta ação não pode ser desfeita.`}
        onConfirm={handleConfirmDelete}
        onCancel={() => setDeleteItem(null)}
        confirmLabel="Excluir"
        loading={deleteMutation.isPending}
      />
    </Box>
  );
};

export default FuelTypesPage;
