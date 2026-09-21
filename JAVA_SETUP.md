# Java Version Issue - Backend Compilation

## Problema
- Backend está configurado para compilar com Java 17
- Seu sistema tem **Java 26 (experimental, lançamento: março 2026)**
- Lombok não suporta Java 26 ainda (erro: `TypeTag :: UNKNOWN`)

## Solução Recomendada: Instalar Java 21 LTS

1. **Download Java 21 LTS**: https://www.oracle.com/java/technologies/downloads/#java21
2. **Instale em seu sistema**
3. **Configure como Java padrão** (adicione ao PATH)
4. **Verifique**: `java -version` deve retornar OpenJDK 21

Depois disso, o comando será:
```bash
cd backend
./mvnw clean compile
./mvnw spring-boot:run
```

## Alternativa: Usar Maven Toolchain (avançado)
Se quiser manter Java 26 como padrão, pode instalar Java 17 separado e configurar Maven Toolchain:
- Veja: https://maven.apache.org/guides/mini/guide-using-toolchains.html

## Status
- ✅ Frontend está pronto para rodar (npm run dev)
- ❌ Backend aguarda Java 21+ ser instalado
- ✅ JWT config, Spring Security, CORS já estão configurados
