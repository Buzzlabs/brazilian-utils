# Guia de Uso

Exemplos práticos de como usar todos os módulos da Brazilian Utils.

## Índice

- [Estados](#estados)
- [Cidades](#cidades)
- [CEP](#cep)
- [CPF e CNPJ](#cpf-e-cnpj)
- [Documentos](#documentos)
- [Telefone](#telefone)
- [Email](#email)
- [Placas Veiculares](#placas-veiculares)
- [Moeda](#moeda)
- [Processo Juridico](#processo-juridico)

---

## Estados

```clojure
(require '[brazilian-utils.states :as states])

; Validar código do estado
(states/valid-uf? :SP)  ; => true
(states/valid-uf? :XX)  ; => false

; Obter nome do estado
(states/uf->state-name :SP)  ; => "São Paulo"
(states/uf->state-name :RJ)  ; => "Rio de Janeiro"

; Obter códigos de área (DDDs)
(states/uf->area-codes :SP)  ; => [11 12 13 14 15 16 17 18 19]

; Obter tamanho da IE (Inscrição Estadual)
(states/uf->ie-length :SP)  ; => 12

; Obter todos os UFs
(states/all-ufs)  ; => [:AC :AL :AP :AM :BA ...]

; Obter nomes de todos os estados
(states/all-state-names)  ; => ["Acre" "Alagoas" ...]
```

## Cidades

```clojure
(require '[brazilian-utils.cities :as cities])

; Obter cidades de um estado
(cities/cities-of :SP)  ; => ["São Paulo" "Campinas" "Sorocaba" ...]
(cities/cities-of :RJ)  ; => ["Rio de Janeiro" "Niterói" ...]

; Versão que lança erro se estado inválido
(cities/cities-of! :SP)  ; => ["São Paulo" "Campinas" ...]
(cities/cities-of! :XX)  ; => lança ExceptionInfo

; Obter todas as cidades com contexto do estado
(cities/all-cities)  ; => [{:state :SP :city "São Paulo"} ...]

; Obter lista plana de nomes de todas as cidades
(cities/all-city-names)  ; => ["São Paulo" "Rio de Janeiro" ...]

; Buscar cidades pelo nome
(cities/find-city-by-name "São Paulo")  ; => [{:state :SP :city "São Paulo"}]

; Verificar se cidade existe em estado
(cities/city-exists? :SP "Campinas")  ; => true
(cities/city-exists? :SP "InvalidCity")  ; => false
```

## CEP

```clojure
(require '[brazilian-utils.cep :as cep])

; Validar CEP (com ou sem formatação)
(cep/is-valid? "01310-100")  ; => true
(cep/is-valid? "01310100")   ; => true
(cep/is-valid? "0131010")    ; => false (muito curto)

; Formatar CEP
(cep/format-cep "01310100")   ; => "01310-100"
(cep/format-cep "01310-100")  ; => "01310-100"
(cep/format-cep "CEP: 01310100")  ; => "01310-100" (remove não-dígitos)

; Integração com ViaCEP (buscar endereço)
(cep/get-address-from-cep "01310-100")
; => {:logradouro "Avenida Paulista" 
;     :bairro "Bela Vista"
;     :localidade "São Paulo"
;     :uf "SP"
;     :cep "01310-100"
;     ...}

; Buscar CEP por endereço (ViaCEP)
(cep/get-cep-information-from-address "Av. Paulista" "São Paulo" "SP")
; => {:cep "01310-100" :logradouro "Avenida Paulista" ...}
```

## CPF e CNPJ

### CPF

```clojure
(require '[brazilian-utils.cpf :as cpf])

; Validar CPF (com ou sem formatação)
(cpf/is-valid? "123.456.789-09")  ; => true/false
(cpf/is-valid? "12345678909")     ; => true/false
(cpf/is-valid? "111.111.111-11")  ; => false (sequência repetida)

; Formatar CPF
(cpf/format-cpf "12345678909")  ; => "123.456.789-09"
(cpf/format-cpf "123.456.789-09")  ; => "123.456.789-09"

; Gerar CPF válido aleatório
(cpf/generate)  ; => "12345678909"

; Gerar CPF com código de UF específico (9º dígito)
(cpf/generate {:uf-code :SP})  ; => CPF com SP (8)
(cpf/generate {:uf-code :RJ})  ; => CPF com RJ (5)

; Obter erros de validação
(cpf/validation-errors "123")  ; => ["Invalid length" ...]
(cpf/validation-errors "123.456.789-09")  ; => [] (válido) ou ["Invalid check digit"]
```

### CNPJ

```clojure
(require '[brazilian-utils.cnpj :as cnpj])

; Validar CNPJ (numérico, padrão anterior)
(cnpj/is-valid? "12.345.678/0001-95")  ; => true/false
(cnpj/is-valid? "12345678000195")     ; => true/false

; Formatar CNPJ
(cnpj/format-cnpj "12345678000195")  ; => "12.345.678/0001-95"

; Gerar CNPJ válido aleatório
(cnpj/generate)  ; => "12345678000195"

; CNPJ Alfanumérico (novo padrão)
(cnpj/is-valid-alfanumeric? "AB1234567000195")  ; => true/false

; Gerar CNPJ alfanumérico
(cnpj/generate-alfanumeric)  ; => "AB1234567000195"

; Validar usando e retornar erros
(cnpj/validation-errors "123")  ; => ["Invalid length" ...]
```

## Documentos

### PIS (Programa de Integração Social)

```clojure
(require '[brazilian-utils.pis :as pis])

; Validar PIS
(pis/is-valid? "123.4567.89-01")  ; => true/false
(pis/is-valid? "12345678901")     ; => true/false

; Formatar PIS
(pis/format-pis "12345678901")  ; => "123.4567.89-01"
```

### CNH (Carteira Nacional de Habilitação)

```clojure
(require '[brazilian-utils.cnh :as cnh])

; Validar CNH
(cnh/is-valid? "12345678901")  ; => true/false

; Obter erros de validação
(cnh/validation-errors "123")  ; => ["Invalid length" ...]
```

### RENAVAM (Registro Nacional de Automóvel)

```clojure
(require '[brazilian-utils.renavam :as renavam])

; Validar RENAVAM
(renavam/is-valid? "12345678901")  ; => true/false

; Formatar RENAVAM
(renavam/format-renavam "12345678901")  ; => "12.345.678-90"
```

### Título Eleitoral

```clojure
(require '[brazilian-utils.titulo-eleitoral :as titulo])

; Validar Título Eleitoral
(titulo/is-valid? "123456789012")  ; => true/false

; Gerar Título Eleitoral válido
(titulo/generate)  ; => "123456789012"
(titulo/generate {:state :SP})  ; => Com código do estado SP

; Formatar
(titulo/format-titulo "123456789012")  ; => "1234.5678.9012"
```

### Inscrição Estadual (IE)

```clojure
(require '[brazilian-utils.inscricao-estadual :as ie])

; Validar IE por estado
(ie/is-valid? :SP "12.345.678.901.23")  ; => true/false

; Formatar IE (formato varia por estado)
(ie/format-ie :SP "123456789012")  ; => "12.345.678.901.23"

; Gerar IE válida para estado
(ie/generate :SP)  ; => "123456789012"
```

## Telefone

```clojure
(require '[brazilian-utils.phone :as phone])

; Validar telefone (genérico)
(phone/is-valid? "(11) 99999-9999")  ; => true
(phone/is-valid? "(11) 3333-4444")   ; => true

; Validar especificamente celular
(phone/is-valid-mobile? "11999999999")  ; => true
(phone/is-valid-mobile? "1133334444")   ; => false (é fixo)

; Validar especificamente telefone fixo
(phone/is-valid-landline? "1133334444")  ; => true
(phone/is-valid-landline? "11999999999")  ; => false (é celular)

; Obter erros de validação
(phone/validation-errors "(11) 99999-9999")  ; => [] (válido)
(phone/validation-errors "123")              ; => ["Invalid area code" ...]

; Formatar telefone
(phone/format-phone "11999999999")  ; => "(11) 99999-9999"
(phone/format-phone "1133334444")   ; => "(11) 3333-4444"
```

## Email

```clojure
(require '[brazilian-utils.email :as email])

; Validar email
(email/is-valid? "user@example.com")      ; => true
(email/is-valid? "invalid.email@")        ; => false
(email/is-valid? "user+tag@domain.co.br") ; => true

; Obter erros de validação
(email/validation-errors "user@example.com")  ; => [] (válido)
(email/validation-errors "invalid")           ; => ["Invalid email format"]
```

## Placas Veiculares

```clojure
(require '[brazilian-utils.license-plate :as plate])

; Validar placa (tradicional ou Mercosul)
(plate/is-valid? "ABC-1234")   ; => true (tradicional)
(plate/is-valid? "ABC1D23")    ; => true (Mercosul)
(plate/is-valid? "INVALID")    ; => false

; Detectar formato
(plate/get-format "ABC1234")   ; => "LLLNNNN" (tradicional)
(plate/get-format "ABC1D23")   ; => "LLLNLNN" (Mercosul)

; Converter para Mercosul
(plate/convert-to-mercosul "ABC1234")  ; => "ABC1B34"
(plate/convert-to-mercosul "ABC1D23")  ; => "ABC1D23" (já é Mercosul)

; Validar especificamente
(plate/is-traditional? "ABC1234")  ; => true
(plate/is-mercosul? "ABC1D23")     ; => true
```

## Moeda

### Moeda (BRL)

```clojure
(require '[brazilian-utils.currency :as currency])

; Formatar como BRL
(currency/format-brl 1000)     ; => "R$ 1.000,00"
(currency/format-brl 1234.50)  ; => "R$ 1.234,50"

; Parse de string BRL
(currency/parse-brl "R$ 1.234,50")  ; => 1234.50
(currency/parse-brl "1.234,50")     ; => 1234.50
```

### Data

```clojure
(require '[brazilian-utils.date :as date])

; Verificar se é feriado nacional
(date/is-holiday? "2024-12-25")  ; => true (Natal)
(date/is-holiday? "2024-12-26")  ; => false

; Listar feriados do ano
(date/holidays-of-year 2024)  ; => ["2024-01-01" "2024-04-21" ...]

; Verificar dias úteis
(date/business-days-between "2024-01-01" "2024-01-31")  ; => número de dias úteis
```

## Capitalização

```clojure
(require '[brazilian-utils.capitalize :as capitalize])

; Capitalizar respeitando regras de português
(capitalize/capitalize "são paulo")       ; => "São Paulo"
(capitalize/capitalize "maria da silva")  ; => "Maria da Silva"

; Preserva maiúsculas importantes
(capitalize/capitalize "joão da SILVA")   ; => "João da Silva"
```

## Processo Jurídico

```clojure
(require '[brazilian-utils.processo-juridico :as processo])

; Validar número de processo (20 dígitos, MOD 97-10)
(processo/is-valid? "0001234567890123456789")  ; => true/false

; Formatar processo: NNNNNNN.DD.AAAA.J.TT.OOOO
(processo/format-processo "0001234567890123456789")
; => "0001234-56.7890.1.23.4567"

; Obter informações do processo
(processo/parse-processo "0001234-56.7890.1.23.4567")
; => {:sequential "0001234"
;     :verification-digits "56"
;     :year "7890"
;     :segment "1"
;     :court "23"
;     :origin "4567"}
```

---

## Dicas Gerais

### Tratamento de Erros

Todos os módulos que têm `validation-errors` retornam uma lista vazia `[]` se válido, ou uma lista de erros se inválido:

```clojure
(phone/validation-errors "(11) 99999-9999")  ; => []
(phone/validation-errors "123")              ; => ["Invalid area code" "Invalid length"]
```

### Leitura Condicional (Clojure/ClojureScript)

A biblioteca funciona em ambas as plataformas usando reader conditionals:

```clojure
#?(:clj  (require '[brazilian-utils.cep :as cep])
   :cljs (require '[brazilian-utils.cep :as cep]))  ; Mesma importação!

; Código compatível com ambas plataformas
(cep/is-valid? "01310-100")  ; Funciona em Clojure e ClojureScript
```

### Performance

Para operações em lote, recomenda-se:

```clojure
; Bom: Validar múltiplos itens
(def cpfs ["123.456.789-09" "987.654.321-00" "111.222.333-44"])
(map cpf/is-valid? cpfs)  ; => [true false true]

; Com tratamento de erros
(map (fn [cpf-str]
       (if (cpf/is-valid? cpf-str)
         {:status :valid :cpf cpf-str}
         {:status :invalid :cpf cpf-str :errors (cpf/validation-errors cpf-str)}))
     cpfs)
```

; Formatar boleto (formato linha digitável)
(boleto/format-linha-digitavel "00190000090114971860168524522114675860000102656")
; => "00190.00009 01149.718601 68524.522114 6 75860000102656"

; Analisar detalhes do boleto
(boleto/parse-boleto "34195.17515 23456.787128 34123.456005 5 10318000002603")
; => {:bank-code "341" :bank-name "Banco Itaú" :currency "9" ...}
```

## Uso em ClojureScript

A API é idêntica em ClojureScript:

```clojure
(ns my-app
  (:require [brazilian-utils.states :as states]
            [brazilian-utils.cities :as cities]
            [brazilian-utils.cep :as cep]))

(states/valid-uf? :SP)
(cities/cities-of :GO)
(cep/is-valid? "01310-100")
```

Funciona da mesma forma em navegadores e Node.js!
