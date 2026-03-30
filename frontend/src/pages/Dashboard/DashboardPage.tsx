import React from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Paper,
  Chip,
} from '@mui/material';
import LocalGasStationIcon from '@mui/icons-material/LocalGasStation';
import EvStationIcon from '@mui/icons-material/EvStation';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import { useFuelTypes } from '../../hooks/useFuelTypes';
import { useFuelPumps } from '../../hooks/useFuelPumps';
import { useFuelings } from '../../hooks/useFuelings';
import LoadingOverlay from '../../components/common/LoadingOverlay';
import { formatCurrency } from '../../utils/formatters';

const COLORS = ['#1976d2', '#f57c00', '#388e3c', '#d32f2f', '#7b1fa2'];

const DashboardPage: React.FC = () => {
  const { data: fuelTypes, isLoading: loadingTypes } = useFuelTypes();
  const { data: fuelPumps, isLoading: loadingPumps } = useFuelPumps();
  const { data: fuelings, isLoading: loadingFuelings } = useFuelings();

  const isLoading = loadingTypes || loadingPumps || loadingFuelings;

  if (isLoading) return <LoadingOverlay message="Carregando dashboard..." />;

  const totalRevenue = fuelings?.reduce((sum, f) => sum + f.totalValue, 0) ?? 0;
  const totalLiters = fuelings?.reduce((sum, f) => sum + f.liters, 0) ?? 0;

  const fuelingsPerPump =
    fuelPumps?.map((pump) => ({
      name: pump.name,
      abastecimentos: fuelings?.filter((f) => f.pump.id === pump.id).length ?? 0,
      litros:
        fuelings
          ?.filter((f) => f.pump.id === pump.id)
          .reduce((s, f) => s + f.liters, 0) ?? 0,
    })) ?? [];

  const fuelTypeDistribution =
    fuelTypes
      ?.map((ft, index) => ({
        name: ft.name,
        value: fuelPumps?.filter((p) => p.fuelTypes.some((t) => t.id === ft.id)).length ?? 0,
        color: COLORS[index % COLORS.length],
      }))
      .filter((d) => d.value > 0) ?? [];

  const recentFuelings = [...(fuelings ?? [])]
    .sort((a, b) => new Date(b.fuelingDate).getTime() - new Date(a.fuelingDate).getTime())
    .slice(0, 5);

  const statCards = [
    {
      title: 'Tipos de Combustível',
      value: fuelTypes?.length ?? 0,
      icon: <LocalGasStationIcon sx={{ fontSize: 40 }} />,
      color: '#1976d2',
      subtitle: 'cadastrados',
    },
    {
      title: 'Bombas',
      value: fuelPumps?.length ?? 0,
      icon: <EvStationIcon sx={{ fontSize: 40 }} />,
      color: '#f57c00',
      subtitle: 'ativas',
    },
    {
      title: 'Abastecimentos',
      value: fuelings?.length ?? 0,
      icon: <ReceiptLongIcon sx={{ fontSize: 40 }} />,
      color: '#388e3c',
      subtitle: 'registrados',
    },
    {
      title: 'Receita Total',
      value: formatCurrency(totalRevenue),
      icon: <AttachMoneyIcon sx={{ fontSize: 40 }} />,
      color: '#7b1fa2',
      subtitle: `${totalLiters.toFixed(3)} L abastecidos`,
    },
  ];

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" mb={3}>
        Visão geral do sistema de gestão do posto
      </Typography>

      <Grid container spacing={3} mb={4}>
        {statCards.map((card) => (
          <Grid item xs={12} sm={6} md={3} key={card.title}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Box display="flex" justifyContent="space-between" alignItems="flex-start">
                  <Box>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      {card.title}
                    </Typography>
                    <Typography variant="h4" fontWeight={700} color={card.color}>
                      {card.value}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {card.subtitle}
                    </Typography>
                  </Box>
                  <Box sx={{ color: card.color, opacity: 0.3 }}>{card.icon}</Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} md={7}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Abastecimentos por Bomba
            </Typography>
            <ResponsiveContainer width="100%" height={280}>
              <BarChart
                data={fuelingsPerPump}
                margin={{ top: 5, right: 10, left: 0, bottom: 5 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis />
                <Tooltip />
                <Bar dataKey="abastecimentos" fill="#1976d2" name="Abastecimentos" />
                <Bar dataKey="litros" fill="#f57c00" name="Litros" />
              </BarChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>

        <Grid item xs={12} md={5}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Bombas por Tipo de Combustível
            </Typography>
            <ResponsiveContainer width="100%" height={280}>
              <PieChart>
                <Pie
                  data={fuelTypeDistribution}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={100}
                  paddingAngle={3}
                  dataKey="value"
                  label={({ name, percent }: { name: string; percent: number }) =>
                    `${name} ${(percent * 100).toFixed(0)}%`
                  }
                  labelLine={false}
                >
                  {fuelTypeDistribution.map((entry) => (
                    <Cell key={entry.name} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Preços por Combustível
            </Typography>
            <Box display="flex" flexWrap="wrap" gap={1} mt={1}>
              {fuelTypes?.map((ft, idx) => (
                <Chip
                  key={ft.id}
                  label={`${ft.name}: ${formatCurrency(ft.pricePerLiter)}/L`}
                  sx={{
                    backgroundColor: COLORS[idx % COLORS.length],
                    color: 'white',
                    fontWeight: 600,
                  }}
                />
              ))}
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Abastecimentos Recentes
            </Typography>
            {recentFuelings.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                Nenhum abastecimento registrado
              </Typography>
            ) : (
              recentFuelings.map((f) => (
                <Box
                  key={f.id}
                  display="flex"
                  justifyContent="space-between"
                  alignItems="center"
                  py={1}
                  borderBottom="1px solid"
                  borderColor="divider"
                  sx={{ '&:last-child': { borderBottom: 'none' } }}
                >
                  <Box>
                    <Typography variant="body2" fontWeight={600}>
                      {f.pump.name}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {f.fuelingDate} • {f.liters.toFixed(3)} L
                    </Typography>
                  </Box>
                  <Typography variant="body2" color="primary" fontWeight={600}>
                    {formatCurrency(f.totalValue)}
                  </Typography>
                </Box>
              ))
            )}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DashboardPage;
