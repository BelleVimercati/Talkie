interface NominatimAddress {
  road?: string;
  house_number?: string;
  suburb?: string;
  neighbourhood?: string;
  city?: string;
  town?: string;
  village?: string;
}

interface NominatimReverseResponse {
  display_name?: string;
  address?: NominatimAddress;
}

// Evita repetir a chamada para o mesmo ponto (5 casas ≈ 1 m)
const cache = new Map<string, string>();

export const geocodingService = {
  reverse: async (
    latitude: number,
    longitude: number,
  ): Promise<string | null> => {
    const key = `${latitude.toFixed(5)},${longitude.toFixed(5)}`;
    const cached = cache.get(key);
    if (cached) return cached;

    const params = new URLSearchParams({
      format: "jsonv2",
      lat: String(latitude),
      lon: String(longitude),
      zoom: "18",
      "accept-language": "pt-BR",
    });
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?${params}`,
    );
    if (!response.ok) return null;

    const data: NominatimReverseResponse = await response.json();
    const a = data.address;
    const street = [a?.road, a?.house_number].filter(Boolean).join(", ");
    const district = a?.suburb ?? a?.neighbourhood;
    const city = a?.city ?? a?.town ?? a?.village;

    // "Rua X, 123 - Bairro - Cidade"; se faltar tudo, usa o texto completo
    const text =
      [street, district, city].filter(Boolean).join(" - ") ||
      data.display_name ||
      null;
    if (text) cache.set(key, text);
    return text;
  },
};
