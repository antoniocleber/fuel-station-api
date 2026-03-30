export const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
    maximumFractionDigits: 3,
  }).format(value);
};

export const formatDate = (dateString: string): string => {
  if (!dateString) return '-';
  // Append time only for plain date strings (YYYY-MM-DD) to avoid UTC timezone shifts
  const normalized = /^\d{4}-\d{2}-\d{2}$/.test(dateString)
    ? dateString + 'T00:00:00'
    : dateString;
  const date = new Date(normalized);
  if (isNaN(date.getTime())) return dateString;
  return new Intl.DateTimeFormat('pt-BR').format(date);
};

export const formatDateTime = (dateTimeString: string): string => {
  if (!dateTimeString) return '-';
  const date = new Date(dateTimeString);
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
};

export const formatLiters = (liters: number): string => {
  return `${liters.toFixed(3).replace('.', ',')} L`;
};
