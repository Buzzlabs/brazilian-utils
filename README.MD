![Logo do Brazilian Utils](https://github.com/brazilian-utils/brand/raw/main/github-hero/github-hero-js.png)

<div align="center">

<p>Utils library for Brazilian-specific businesses.</p>

<p><strong>Clojure</strong> • <strong>ClojureScript</strong></p>

[📖 Documentation](#installation)

[![Build Status](https://github.com/Buzzlabs/brazilian-utils/workflows/Tests/badge.svg)](https://github.com/Buzzlabs/brazilian-utils/actions?query=workflow%3ATests) [![Code Quality](https://github.com/Buzzlabs/brazilian-utils/workflows/Code%20Quality/badge.svg)](https://github.com/Buzzlabs/brazilian-utils/actions?query=workflow%3ACode%20Quality) [![Clojars](https://img.shields.io/clojars/v/br.com.buzzlabs/brazilian-utils.svg)](https://clojars.org/br.com.buzzlabs/brazilian-utils) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

</div>

**Table of Contents**

- [Installation](#installation)
  - [Clojure](#clojure)
  - [ClojureScript](#clojurescript)
- [Usage](#usage)
  - [Clojure](#clojure-1)
  - [ClojureScript](#clojurescript-1)
  - [Modules](#modules)
  - [Quick Examples](#quick-examples)
    - [States \& Cities](#states--cities)
    - [CPF \& CNPJ](#cpf--cnpj)
    - [Phone \& Email](#phone--email)
    - [CEP \& Address](#cep--address)
    - [License Plates](#license-plates)
  - [Documentation](#documentation)
    - [Setup](#setup)
    - [Run Tests](#run-tests)
    - [Code Style](#code-style)
  - [License](#license)

# Installation

## Clojure

Add to your `deps.edn`:

```clojure
{:deps
 {com.github.Buzzlabs/brazilian-utils {:mvn/version "0.1.0"}}}
```

Or using Leiningen in your `project.clj`:

```clojure
:dependencies [[com.github.Buzzlabs/brazilian-utils "0.1.0"]]
```

## ClojureScript

Same as Clojure - add to your `deps.edn` or `project.clj` as shown above. Works with Shadow-CLJS, Figwheel, or any ClojureScript compiler.

# Usage

## Clojure

```clojure
(require '[brazilian-utils.states :as states]
         '[brazilian-utils.cities :as cities]
         '[brazilian-utils.cep :as cep]
         '[brazilian-utils.cpf :as cpf]
         '[brazilian-utils.cnpj :as cnpj]
         '[brazilian-utils.phone :as phone])

(states/valid-uf? :SP)                            ; => true
(states/uf->state-name :SP)                       ; => "São Paulo"
(cities/cities-of :GO)                            ; => ["Goiânia" "Anápolis" ...]
(cep/is-valid? "01310-100")                       ; => true
(cep/format-cep "01310100")                       ; => "01310-100"
(cpf/is-valid? "123.456.789-09")                  ; => true/false
(cpf/generate)                                    ; => "12345678909" (válido)
(cnpj/generate)                                   ; => "12345678000195" (válido)
(phone/is-valid-mobile? "11999999999")            ; => true
```

## ClojureScript

```clojure
(ns my-app
  (:require [brazilian-utils.states :as states]
            [brazilian-utils.cities :as cities]
            [brazilian-utils.cep :as cep]
            [brazilian-utils.cpf :as cpf]
            [brazilian-utils.cnpj :as cnpj]
            [brazilian-utils.phone :as phone]))

(states/valid-uf? :SP)                            ; => true
(states/uf->state-name :SP)                       ; => "São Paulo"
(cities/cities-of :GO)                            ; => ["Goiânia" "Anápolis" ...]
(cep/is-valid? "01310-100")                       ; => true
(cpf/is-valid? "123.456.789-09")                  ; => true/false
(phone/is-valid-mobile? "11999999999")            ; => true
```

---

## Modules

Brazilian Utils provides validation, formatting, and generation utilities for:

- **[States](docs/modules/states.md)** - UF codes, state names, area codes
- **[Cities](docs/modules/cities.md)** - Cities by state, city lookup
- **[CEP](docs/modules/cep.md)** - Postal codes, address lookup via ViaCEP
- **[CPF](docs/modules/cpf.md)** - Brazilian personal ID validation & generation
- **[CNPJ](docs/modules/cnpj.md)** - Brazilian business ID validation & generation (numeric and alphanumeric)
- **[PIS](docs/modules/pis.md)** - Social integration program number validation
- **[CNH](docs/modules/cnh.md)** - Driver's license validation
- **[RENAVAM](docs/modules/renavam.md)** - Vehicle registration validation
- **[Título Eleitoral](docs/modules/titulo_eleitoral.md)** - Voter ID validation & generation
- **[Inscrição Estadual](docs/modules/inscricao_estadual.md)** - State registration (IE) by UF
- **[Email](docs/modules/email.md)** - Email format validation
- **[Phone](docs/modules/phone.md)** - Brazilian phone number validation (mobile & landline)
- **[Capitalize](docs/modules/capitalize.md)** - Smart capitalization with Portuguese rules
- **[Currency](docs/modules/currency.md)** - Brazilian Real (BRL) formatting & parsing
- **[License Plate](docs/modules/license_plate.md)** - Traditional & Mercosul plate validation & conversion
- **[Processo Jurídico](docs/modules/processo_juridico.md)** - Court case number validation

For complete documentation, see [docs/index.md](docs/index.md).

---

## Quick Examples

### States & Cities

```clojure
(require '[brazilian-utils.states :as states])
(require '[brazilian-utils.cities :as cities])

; States
(states/valid-uf? :SP)                    ; => true
(states/uf->state-name :SP)               ; => "São Paulo"
(states/uf->area-codes :SP)               ; => [11 12 13 14 15 16 17 18 19]

; Cities
(cities/cities-of :SP)                    ; => ["São Paulo" "Campinas" ...]
(cities/find-city-by-name "São Paulo")    ; => [{:state :SP :city "São Paulo"}]
(cities/city-exists? :SP "Campinas")      ; => true
```

### CPF & CNPJ

```clojure
(require '[brazilian-utils.cpf :as cpf])
(require '[brazilian-utils.cnpj :as cnpj])

; CPF
(cpf/is-valid? "123.456.789-09")          ; => true/false
(cpf/format-cpf "12345678909")            ; => "123.456.789-09"
(cpf/generate)                            ; => "12345678909" (valid random)
(cpf/generate {:uf-code :SP})             ; => CPF with SP as 9th digit

; CNPJ (numeric)
(cnpj/is-valid? "12.345.678/0001-95")    ; => true/false
(cnpj/format-cnpj "12345678000195")      ; => "12.345.678/0001-95"
(cnpj/generate)                           ; => "12345678000195" (valid random)

; CNPJ (alphanumeric - newer standard)
(cnpj/generate-alfanumeric)               ; => "AB1234567000195"
```

### Phone & Email

```clojure
(require '[brazilian-utils.phone :as phone])
(require '[brazilian-utils.email :as email])

; Phone
(phone/is-valid? "(11) 99999-9999")       ; => true
(phone/is-valid-mobile? "11999999999")    ; => true
(phone/is-valid-landline? "1133334444")   ; => true
(phone/validation-errors "123")           ; => ["Invalid area code" ...]

; Email
(email/is-valid? "user@example.com")      ; => true
```

### CEP & Address

```clojure
(require '[brazilian-utils.cep :as cep])

(cep/is-valid? "01310-100")                                   ; => true
(cep/format-cep "01310100")                                   ; => "01310-100"
(cep/get-address-from-cep "01310-100")                        ; => {:logradouro "..." :localidade "São Paulo" ...}
(cep/get-cep-information-from-address "Av. Paulista" "São Paulo" "SP") ; => {:cep "01310-100" ...}
```

### License Plates

```clojure
(require '[brazilian-utils.license-plate :as plate])

; Validation
(plate/is-valid? "ABC-1234")              ; => true (traditional)
(plate/is-valid? "ABC1D23")               ; => true (Mercosul)

; Format detection
(plate/get-format "ABC1234")              ; => "LLLNNNN"
(plate/get-format "ABC1D23")              ; => "LLLNLNN"

; Conversion
(plate/convert-to-mercosul "ABC1234")     ; => "ABC1B34"
```

---

## Documentation

- **[API Reference](docs/api-reference.md)** - Complete API documentation
- **[Contributing](docs/contributing.md)** - How to contribute
- **[Module Guides](docs/guides/)** - Usage guides for each module

### Setup

```bash
git clone https://github.com/Buzzlabs/brazilian-utils.git
cd brazilian-utils
```

### Run Tests

```bash
# Clojure (JVM)
clojure -M:test

# ClojureScript (Node.js)
clojure -M:cljs test

# Watch mode - recompila automaticamente quando arquivos mudam
clojure -M:cljs watch test

# REPL ClojureScript no browser
clojure -M:cljs watch :browser
```

### Code Style

We use `cljfmt` for code formatting. Your PR should follow these style guidelines.

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/anabastos">
        <img src="https://avatars.githubusercontent.com/u/10088900?v=4" width="100px;" alt=""/>
        <br />
        <sub><b>Ana Bastos</b></sub>
      </a>
      <br />
      <a href="https://github.com/brazilian-utils/brazilian-utils/commits?author=anabastos" title="Code">💻</a>
    </td>
  </tr>
</table>

## License

[MIT](LICENSE)
