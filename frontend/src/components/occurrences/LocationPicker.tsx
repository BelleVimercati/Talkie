import { useEffect, useState } from "react";
import {
  MapContainer,
  Marker,
  TileLayer,
  useMap,
  useMapEvents,
} from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import iconUrl from "leaflet/dist/images/marker-icon.png";
import iconRetinaUrl from "leaflet/dist/images/marker-icon-2x.png";
import shadowUrl from "leaflet/dist/images/marker-shadow.png";
import { LocateFixed, X } from "lucide-react";
import { useGeolocation } from "@/hooks/useGeolocation";

const markerIcon = L.icon({
  iconUrl,
  iconRetinaUrl,
  shadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});

// Centro inicial quando ainda não há ponto: Niterói
const DEFAULT_CENTER: [number, number] = [-22.8832, -43.1034];

interface LocationPickerProps {
  latitude?: number;
  longitude?: number;
  onChange?: (latitude?: number, longitude?: number) => void;
  readOnly?: boolean;
}

/* Clique no mapa posiciona o pino */
function ClickToPlace({
  onPick,
}: {
  onPick: (lat: number, lng: number) => void;
}) {
  useMapEvents({ click: (e) => onPick(e.latlng.lat, e.latlng.lng) });
  return null;
}

/* Move o mapa quando o ponto muda */
function FollowPoint({
  latitude,
  longitude,
}: {
  latitude?: number;
  longitude?: number;
}) {
  const map = useMap();
  useEffect(() => {
    if (latitude !== undefined && longitude !== undefined) {
      map.setView([latitude, longitude], Math.max(map.getZoom(), 17));
    }
  }, [map, latitude, longitude]);
  return null;
}

export function LocationPicker({
  latitude,
  longitude,
  onChange,
  readOnly = false,
}: LocationPickerProps) {
  const { locate, isLocating, error } = useGeolocation();
  const [accuracy, setAccuracy] = useState<number | null>(null);
  const hasPoint = latitude !== undefined && longitude !== undefined;

  const handleLocate = async () => {
    try {
      const position = await locate();
      setAccuracy(position.accuracy);
      onChange?.(position.latitude, position.longitude);
    } catch {
      // a mensagem já está em `error`
    }
  };

  // Ponto escolhido à mão: a precisão do GPS deixa de valer
  const pick = (lat: number, lng: number) => {
    setAccuracy(null);
    onChange?.(lat, lng);
  };

  return (
    <div className="space-y-2">
      {!readOnly && (
        <div className="flex flex-wrap items-center gap-3">
          <button
            type="button"
            onClick={handleLocate}
            disabled={isLocating}
            className="flex items-center gap-2 rounded-md bg-brand-blue px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
          >
            <LocateFixed size={16} />
            {isLocating ? "Localizando..." : "Usar minha localização"}
          </button>
          {hasPoint && (
            <button
              type="button"
              onClick={() => onChange?.(undefined, undefined)}
              className="flex items-center gap-1 text-sm text-black-500 hover:text-black-700"
            >
              <X size={14} /> Remover ponto
            </button>
          )}
          <span className="text-xs text-black-500">
            ou clique no mapa / arraste o pino
          </span>
        </div>
      )}

      {error && <p className="text-xs text-red-500">{error}</p>}
      {accuracy !== null && (
        <p
          className={`text-xs ${accuracy > 100 ? "text-brand-orange" : "text-black-500"}`}
        >
          Precisão aproximada: {Math.round(accuracy)} m
          {accuracy > 100 && " — confira e ajuste o pino no mapa."}
        </p>
      )}

      {/* O mapa PRECISA de altura definida, senão fica invisível */}
      <MapContainer
        center={hasPoint ? [latitude, longitude] : DEFAULT_CENTER}
        zoom={hasPoint ? 17 : 13}
        scrollWheelZoom={!readOnly}
        className={`w-full rounded-md ${readOnly ? "h-48" : "h-72"}`}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {latitude !== undefined && longitude !== undefined && (
          <Marker
            position={[latitude, longitude]}
            icon={markerIcon}
            draggable={!readOnly}
            eventHandlers={{
              dragend: (e) => {
                const { lat, lng } = (e.target as L.Marker).getLatLng();
                pick(lat, lng);
              },
            }}
          />
        )}
        {!readOnly && <ClickToPlace onPick={pick} />}
        <FollowPoint latitude={latitude} longitude={longitude} />
      </MapContainer>
    </div>
  );
}

export default LocationPicker;
