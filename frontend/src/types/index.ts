export interface FuelType {
  id: number;
  name: string;
  pricePerLiter: number;
  createdAt: string;
  updatedAt: string;
}

export interface FuelTypeSummary {
  id: number;
  name: string;
  pricePerLiter: number;
}

export interface FuelPump {
  id: number;
  name: string;
  fuelTypes: FuelTypeSummary[];
  createdAt: string;
  updatedAt: string;
}

export interface FuelPumpSummary {
  id: number;
  name: string;
  fuelTypes: FuelTypeSummary[];
}

export interface Fueling {
  id: number;
  fuelingDate: string;
  liters: number;
  totalValue: number;
  pump: FuelPumpSummary;
  createdAt: string;
  updatedAt: string;
}

export interface FuelTypeRequest {
  name: string;
  pricePerLiter: number;
}

export interface FuelPumpRequest {
  name: string;
  fuelTypeIds: number[];
}

export interface FuelingRequest {
  pumpId: number;
  fuelingDate: string;
  liters: number;
  totalValue: number;
}

export interface FuelingsFilters {
  pumpId?: number;
  startDate?: string;
  endDate?: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
