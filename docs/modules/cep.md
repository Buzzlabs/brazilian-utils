# CEP - Código de Endereçamento Postal

Módulo para validação, formatação e busca de informações de endereço através de CEP.

## Visão Geral

Este módulo fornece funções para:

- Validar CEP com ou sem formatação
- Formatar CEP em padrão brasileiro
- Buscar endereço completo via ViaCEP API
- Buscar CEP por endereço via ViaCEP API
- Obter informações sobre erros de validação

## Estrutura do CEP

O CEP segue o formato: **XXXXX-XXX**

- **5 primeiros dígitos**: Região + subregião
- **3 últimos dígitos**: Setor + subsetor
- Exemplo: 01310-100 (Avenida Paulista, São Paulo - SP)

## Funções Principais

### is-valid?

```clojure
(cep/is-valid? cep-string)
```

Valida um CEP com ou sem formatação.

**Argumentos:**
- `cep-string` (string): CEP a validar, com ou sem formatação

**Retorna:**
- `true` se válido, `false` caso contrário

**Exemplos:**
```clojure
(cep/is-valid? "01310-100")   ; => true
(cep/is-valid? "01310100")    ; => true
(cep/is-valid? "0131010")     ; => false (muito curto)
(cep/is-valid? "01310-10")    ; => false (incompleto)
(cep/is-valid? "abcdefgh")    ; => false (não numérico)
```

**Validação:**
- Deve conter exatamente 8 dígitos
- Aceita com ou sem hífen
- Remove automaticamente caracteres de formatação

### format-cep

```clojure
(cep/format-cep cep-string)
```

Formata um CEP no padrão brasileiro XXXXX-XXX.

**Argumentos:**
- `cep-string` (string): CEP a formatar (com ou sem formatação)

**Retorna:**
- String com CEP formatado como XXXXX-XXX

**Exemplos:**
```clojure
(cep/format-cep "01310100")    ; => "01310-100"
(cep/format-cep "01310-100")   ; => "01310-100"
(cep/format-cep "CEP: 01310100")  ; => "01310-100" (remove não-dígitos)
(cep/format-cep "01310")       ; => "01310" (parcial)
```

### get-address-from-cep

```clojure
(cep/get-address-from-cep cep-string)
```

Busca informações de endereço completo através da ViaCEP API.

**Argumentos:**
- `cep-string` (string): CEP a buscar (com ou sem formatação)

**Retorna:**
- Map com informações do endereço:
  ```clojure
  {:cep "01310-100"
   :logradouro "Avenida Paulista"
   :complemento ""
   :bairro "Bela Vista"
   :localidade "São Paulo"
   :uf "SP"
   :ibge "3550308"
   :gia ""
   :ddd "11"
   :siafi "7107"}
  ```
- `nil` se CEP não encontrado

**Exemplos:**
```clojure
; Clojure
(cep/get-address-from-cep "01310-100")
; => {:cep "01310-100" :logradouro "Avenida Paulista" :localidade "São Paulo" :uf "SP" ...}

; ClojureScript (retorna Promise)
(-> (cep/get-address-from-cep "01310-100")
    (.then (fn [address] (println address))))
```

**Notas:**
- Requer conexão com internet (chama ViaCEP API)
- Em ClojureScript, retorna uma Promise
- Em Clojure, é síncrono

### get-cep-information-from-address

```clojure
(cep/get-cep-information-from-address logradouro localidade uf)
```

Busca CEP por endereço através da ViaCEP API.

**Argumentos:**
- `logradouro` (string): Nome da rua/avenida
- `localidade` (string): Nome da cidade
- `uf` (string): Código do estado (2 letras, ex: "SP")

**Retorna:**
- Vector com CEPs encontrados (pode retornar múltiplos resultados)
- Cada item é um map com informações do endereço
- `[]` se nenhum resultado encontrado

**Exemplos:**
```clojure
; Clojure
(cep/get-cep-information-from-address "Avenida Paulista" "São Paulo" "SP")
; => [{:cep "01310-100" :logradouro "Avenida Paulista" ...}
;     {:cep "01311-200" :logradouro "Avenida Paulista" ...}]

; ClojureScript (retorna Promise)
(-> (cep/get-cep-information-from-address "Avenida Paulista" "São Paulo" "SP")
    (.then (fn [results] (println results))))
```

### validation-errors

```clojure
(cep/validation-errors cep-string)
```

Retorna uma lista de erros de validação do CEP.

**Argumentos:**
- `cep-string` (string): CEP a validar

**Retorna:**
- Vector com lista de erros (vazio se válido)

**Exemplos:**
```clojure
(cep/validation-errors "01310-100")  ; => []
(cep/validation-errors "123")        ; => ["Invalid length"]
(cep/validation-errors "ABCDE-FGH")  ; => ["Invalid CEP format"]
```

## Recursos

- ✅ Validação com ou sem formatação
- ✅ Formatação automática
- ✅ Integração com ViaCEP para busca de endereços
- ✅ Busca de CEP por endereço
- ✅ Mensagens de erro detalhadas
- ✅ Cross-platform (Clojure & ClojureScript)
- ✅ Suporte a Promises em ClojureScript

## Tratamento de Erros

```clojure
(cep/validation-errors "0131010")
; => ["Invalid length"]

(cep/validation-errors "ABCDE-FGH")
; => ["Invalid CEP format"]

; Tratamento de erro em requisição ViaCEP
(try
  (cep/get-address-from-cep "00000-000")  ; CEP válido mas pode não existir
  (catch Exception e
    (println "Erro ao buscar CEP:" (.getMessage e))))
```

## Casos de Uso

### Validar e formatar entrada de CEP

```clojure
(require '[brazilian-utils.cep :as cep])

(defn validar-cep-input [cep-input]
  (if (cep/is-valid? cep-input)
    {:status :valid :cep (cep/format-cep cep-input)}
    {:status :invalid :errors (cep/validation-errors cep-input)}))

(validar-cep-input "01310100")
; => {:status :valid :cep "01310-100"}

(validar-cep-input "123")
; => {:status :invalid :errors ["Invalid length"]}
```

### Buscar endereço completo (Clojure)

```clojure
(defn find-address [cep-string]
  (let [formatted-cep (cep/format-cep cep-string)]
    (if (cep/is-valid? formatted-cep)
      (let [address (cep/get-address-from-cep formatted-cep)]
        (if address
          {:status :found :address address}
          {:status :not-found :cep formatted-cep}))
      {:status :invalid :errors (cep/validation-errors cep-string)})))

(find-address "01310100")
; => {:status :found :address {:cep "01310-100" :logradouro "..." ...}}
```

### Buscar endereço completo (ClojureScript)

```clojure
(defn find-address [cep-string]
  (-> (cep/get-address-from-cep cep-string)
      (.then (fn [address]
               (if address
                 {:status :found :address address}
                 {:status :not-found :cep cep-string})))
      (.catch (fn [error]
                {:status :error :message (.-message error)}))))

(find-address "01310100")
```

### Buscar CEP por endereço

```clojure
(defn find-ceps [street city state]
  (let [results (cep/get-cep-information-from-address street city state)]
    {:query {:street street :city city :state state}
     :results results
     :count (count results)}))

(find-ceps "Avenida Paulista" "São Paulo" "SP")
; => {:query {...} :results [...] :count 5}
```

### Autocomplete de CEP em formulário

```clojure
(defn on-cep-blur [cep-value form-state]
  (if (cep/is-valid? cep-value)
    (let [address (cep/get-address-from-cep cep-value)]
      (merge form-state
             {:cep (cep/format-cep cep-value)
              :street (:logradouro address)
              :city (:localidade address)
              :state (:uf address)
              :neighborhood (:bairro address)}))
    form-state))
```


## Dependências Externas

O módulo utiliza a **ViaCEP API** (gratuita e sem autenticação) para busca de endereços:

- Endpoint: `https://viacep.com.br/ws/{cep}/json/`
- Limite: 1 requisição por CEP válido, sem limite de requisições por IP
- Documentação: https://viacep.com.br/

## Ver Também

- [Estados](states.md) - Para validação de UF
- [Cidades](cities.md) - Para lista de cidades por estado
