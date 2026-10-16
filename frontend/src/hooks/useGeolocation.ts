import { useCallback, useState } from "react";

export interface GeoPosition {
  latitude: number;
  longitude: number;
  accuracy: number;
}

// Códigos definidos pela API: 1 = negado, 2 = indisponível, 3 = tempo esgotado
const ERROR_MESSAGES: Record<number, string> = {
  1: "Permissão de localização negada. Libere nas configurações do navegador ou marque o ponto no mapa.",
  2: "Não foi possível determinar sua localização.",
  3: "A localização demorou demais para responder. Tente novamente.",
};

export function useGeolocation() {
  const [isLocating, setIsLocating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /* confere se o navegador aceita usar geolocalização */
  const locate = useCallback((): Promise<GeoPosition> => {
    if (!("geolocation" in navigator) || !window.isSecureContext) {
      const message =
        "Seu navegador não permite obter a localização nessa página.";
      setError(message);
      return Promise.reject(new Error(message));
    }

    setIsLocating(true);
    setError(null);

    /* devolve a localização para quem chama o hook */
    return new Promise((resolve, reject) => {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setIsLocating(false);
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            accuracy: position.coords.accuracy,
          });
        },
        (err) => {
          const message =
            ERROR_MESSAGES[err.code] ?? "Erro ao obter a localização.";
          setIsLocating(false);
          setError(message);
          reject(new Error(message));
        },
        { enableHighAccuracy: true, timeout: 10000, maximumAge: 60000 },
      );
    });
  }, []);

  return { locate, isLocating, error };
}
