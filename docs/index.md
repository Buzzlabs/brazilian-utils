# Brazilian Utils

**Brazilian Utils** é uma biblioteca completa de validação, formatação e geração de dados brasileiros. Desenvolvida em Clojure/ClojureScript, oferece suporte total para JVM, navegadores e Node.js.

## ✨ Características Principais

- **Estados & Cidades** - UF codes, state names, area codes, city lists
- **Documentos** - CPF, CNPJ (numérico e alfanumérico), PIS, CNH, RENAVAM, Título Eleitoral, IE
- **Endereços** - CEP validation, ViaCEP integration, postal code formatting
- **Comunicação** - Phone validation (mobile/landline), Email validation
- **Placas** - Traditional & Mercosul license plate validation & conversion
- **Processos Jurídicos** - Court case number validation (MOD 97-10)
- **Utilitários** - Currency formatting, Date handling, Smart capitalization
- **Geração** - CPF/CNPJ/Título Eleitoral generation with validation

## 📦 Plataformas Suportadas

| Plataforma | Suporte |
|-----------|---------|
| Clojure (JVM) | ✅ Completo (1.9+) |
| ClojureScript | ✅ Completo (Navegador e Node.js) |

## 🚀 Início Rápido

### Estados & Cidades

```clojure
(require '[brazilian-utils.states :as states]
         '[brazilian-utils.cities :as cities])

(states/valid-uf? :SP)                    ; => true
(states/uf->state-name :SP)               ; => "São Paulo"
(states/uf->area-codes :SP)               ; => [11 12 13 14 15 16 17 18 19]

(cities/cities-of :SP)                    ; => ["São Paulo" "Campinas" ...]
(cities/city-exists? :SP "Campinas")      ; => true
```

### CPF & CNPJ

```clojure
(require '[brazilian-utils.cpf :as cpf]
         '[brazilian-utils.cnpj :as cnpj])

; CPF Validation & Generation
(cpf/is-valid? "123.456.789-09")          ; => true/false
(cpf/generate)                            ; => "12345678909" (random valid)
(cpf/format-cpf "12345678909")            ; => "123.456.789-09"

; CNPJ Validation & Generation
(cnpj/is-valid? "12.345.678/0001-95")    ; => true/false
(cnpj/generate)                           ; => "12345678000195" (random valid)
(cnpj/generate-alfanumeric)               ; => "AB1234567000195"
```

### CEP & Endereços

```clojure
(require '[brazilian-utils.cep :as cep])

(cep/is-valid? "01310-100")               ; => true
(cep/format-cep "01310100")               ; => "01310-100"

; Via ViaCEP
(cep/get-address-from-cep "01310-100")   ; => {:logradouro "Av. Paulista" :localidade "São Paulo" ...}
```

### Telefone & Email

```clojure
(require '[brazilian-utils.phone :as phone]
         '[brazilian-utils.email :as email])

(phone/is-valid? "(11) 99999-9999")       ; => true
(phone/is-valid-mobile? "11999999999")    ; => true
(email/is-valid? "user@example.com")      ; => true
```

### Placas Veiculares

```clojure
(require '[brazilian-utils.license-plate :as plate])

(plate/is-valid? "ABC-1234")              ; => true (traditional)
(plate/is-valid? "ABC1D23")               ; => true (Mercosul)
(plate/convert-to-mercosul "ABC1234")     ; => "ABC1B34"
```

## 📚 Documentação Completa

- **[API Reference](api-reference.md)** - Documentação completa de todas as funções
- **[Installation Guide](guides/installation.md)** - Como instalar e configurar
- **[Usage Guide](guides/usage.md)** - Exemplos e padrões de uso

## 🔗 Todos os Módulos

[Ver documentação completa dos módulos →](api-reference.md)

| Módulo | Descrição |
|--------|-----------|
| **Estados** | UF codes, state names, area codes |
| **Cidades** | Cities by state, city lookup |
| **CEP** | Postal code validation & formatting |
| **CPF** | Personal ID validation & generation |
| **CNPJ** | Business ID (numeric & alphanumeric) |
| **PIS** | Social integration program validation |
| **CNH** | Driver's license validation |
| **RENAVAM** | Vehicle registration validation |
| **Título Eleitoral** | Voter ID validation & generation |
| **Inscrição Estadual** | State registration by UF |
| **Email** | Email format validation |
| **Telefone** | Phone validation (mobile & landline) |
| **Capitalização** | Smart text capitalization |
| **Moeda** | Brazilian Real formatting |
| **Placa de Veículo** | Plate validation & Mercosul conversion |
| **Processo Jurídico** | Court case number validation |

## 💡 Exemplos Avançados

### Tratamento de Erros

```clojure
(require '[brazilian-utils.phone :as phone])

(phone/validation-errors "(11) 99999-9999")    ; => [] (valid)
(phone/validation-errors "123")                ; => ["Invalid area code" ...]
```

### Integração ViaCEP

```clojure
(require '[brazilian-utils.cep :as cep])

(cep/get-cep-information-from-address 
  "Av. Paulista" "São Paulo" "SP")
; => {:cep "01310-100" :logradouro "Av. Paulista" ...}
```

### Geração de Documentos

```clojure
(require '[brazilian-utils.cpf :as cpf]
         '[brazilian-utils.cnpj :as cnpj])

; CPF with specific UF code
(cpf/generate {:uf-code :SP})              ; => CPF with :SP rules

; CNPJ alphanumeric (newer format)
(cnpj/generate-alfanumeric)                ; => "AB1234567000195"
```

## 🤟 Contributing

Sua contribuição é bem-vinda! Abra uma issue ou pull request no repositório do GitHub.

## 📄 Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](https://github.com/Buzzlabs/brazilian-utils/blob/master/LICENSE) para detalhes.

## 🔗 Links Úteis

- [Documentação Completa](api-reference.md)
- [GitHub Repository](https://github.com/Buzzlabs/brazilian-utils)
- [Clojars](https://clojars.org/br.com.buzzlabs/brazilian-utils)
