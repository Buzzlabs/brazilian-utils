# Título Eleitoral - Validação de Título de Eleitor

Módulo para validação de Título Eleitoral (Título de Eleitor) com suporte a geração de números válidos, extração de código de estado e validação de dígitos verificadores.

## Visão Geral

O módulo `titulo-eleitoral` fornece validação completa de Títulos Eleitorais brasileiros, com suporte a:
- Validação de números com 12 dígitos
- Verificação de dígitos verificadores
- Extração de código de estado (UF)
- Geração de números válidos
- Remoção de caracteres de formatação

**Principais capacidades:**
- ✅ Validação de Título Eleitoral com 12 dígitos
- ✅ Verificação de 2 dígitos verificadores
- ✅ Extração de código de estado
- ✅ Geração de números válidos aleatoriamente
- ✅ Suporte a múltiplos formatos de entrada
- ✅ Detecção de erros específicos

## Estrutura

O Título Eleitoral é um número de 12 dígitos que identifica um eleitor:

```
XXXXXXXXXXXX
```

**Formato com formatação:**
```
XXXX XXXX XXXX
```

**Estrutura dos dígitos:**
- **Primeiros 8 dígitos (000001-999999)**: Número sequencial do eleitor
- **Dígitos 9-10**: Dígito verificador (DDV)
- **Dígitos 11-12**: Código de estado (UF) - valores de 01 a 28

**Códigos de Estado:**
- 01-03: DF, AC, AL, AP
- 04-07: AM, BA, CE, DF
- 08-11: ES, GO, MA, MG
- 12-15: MT, MS, PA, PB
- 16-19: PE, PI, PR, RJ
- 20-23: RN, RO, RR, RS
- 24-28: SC, SE, SP, TO, Exterior

## Algoritmo de Validação

A validação utiliza 2 dígitos verificadores:

1. **Primeiro dígito verificador (DDV)**: 
   - Multiplica primeiros 8 dígitos por (2,3,4,5,6,7,8,9)
   - Módulo 11 da soma
   
2. **Segundo dígito verificador**:
   - Multiplica dígitos 9-11 por (7,8,9)
   - Módulo 11 da soma

3. **Validação de UF**: Código deve estar entre 01 e 28

## Funções Principais

### `is-valid?`

Valida se um Título Eleitoral é válido segundo o algoritmo oficial.

**Assinatura:**
```clojure
(is-valid? voter-id)
```

**Argumentos:**
- `voter-id` - String com Título Eleitoral (formatada ou não)

**Retorna:** Boolean - `true` se for válido, `false` caso contrário

**Exemplos:**
```clojure
(is-valid? "123456789035")      ; => true/false (depende de dígitos verificadores)
(is-valid? "1234 5678 9035")    ; => true/false (com formatação)
(is-valid? "1234-5678-9035")    ; => true/false (com hífen)
(is-valid? "123456789099")      ; => false (UF 99 inválido)
(is-valid? "00000000000000")    ; => false (dígitos repetidos)
(is-valid? "12345678")          ; => false (8 dígitos)
(is-valid? nil)                 ; => false
(is-valid? "")                  ; => false
```

### `validation-errors`

Valida um Título Eleitoral e retorna erros específicos se inválido.

**Assinatura:**
```clojure
(validation-errors voter-id)
```

**Argumentos:**
- `voter-id` - String com Título Eleitoral

**Retorna:** Vetor de strings com mensagens de erro (vazio se válido)

**Exemplos:**
```clojure
(validation-errors "123456789035")
; => []

(validation-errors "123456789099")
; => ["Invalid UF code"]

(validation-errors "12345678")
; => ["Voter ID must have 12 digits after cleaning"]

(validation-errors "00000000000000")
; => ["All digits cannot be the same"]
```

### `remove-symbols`

Remove todos os caracteres não numéricos de um Título Eleitoral.

**Assinatura:**
```clojure
(remove-symbols voter-id)
```

**Argumentos:**
- `voter-id` - String com Título Eleitoral (formatada ou não); nil permitido

**Retorna:** String contendo apenas dígitos

**Exemplos:**
```clojure
(remove-symbols "1234 5678 9035")      ; => "123456789035"
(remove-symbols "1234-5678-9035")      ; => "123456789035"
(remove-symbols "123456789035")        ; => "123456789035"
(remove-symbols nil)                   ; => ""
(remove-symbols "")                    ; => ""
```

### `get-uf-code`

Extrai o código de estado (UF) de um Título Eleitoral.

**Assinatura:**
```clojure
(get-uf-code voter-id)
```

**Argumentos:**
- `voter-id` - String com Título Eleitoral (formatada ou não)

**Retorna:** String com 2 dígitos do código UF ou `nil` se inválido

**Exemplos:**
```clojure
(get-uf-code "123456789035")   ; => "35"
(get-uf-code "1234 5678 9035") ; => "35"
(get-uf-code "invalid")        ; => nil
(get-uf-code "12345678")       ; => nil (muito curto)
```

### `generate`

Gera um Título Eleitoral válido aleatoriamente.

**Assinatura:**
```clojure
(generate)
(generate options)
```

**Argumentos:**
- `options` - Mapa opcional com:
  - `:uf-code` => Código de estado (string "01"-"28" ou integer 1-28)

**Retorna:** String com Título Eleitoral válido de 12 dígitos ou `nil` se falhar

**Exemplos:**
```clojure
(generate)
; => "123456780104" (UF aleatório)

(generate {:uf-code 35})
; => "234567891235" (São Paulo - UF 35)

(generate {:uf-code "01"})
; => "345678902301" (DF - UF 01)

(is-valid? (generate))
; => true

(is-valid? (generate {:uf-code 35}))
; => true
```

## Recursos

- ✅ Validação completa com 2 dígitos verificadores
- ✅ Verificação de código de estado válido (01-28)
- ✅ Detecção de sequências de dígitos repetidos
- ✅ Extração de informação de estado
- ✅ Geração de números válidos
- ✅ Suporte a múltiplos formatos de entrada
- ✅ Mensagens de erro específicas
- ✅ **Nota importante**: Valida estrutura e matemática, NÃO verifica existência no TSE

## Tratamento de Erros

O módulo fornece feedback detalhado sobre problemas:

```clojure
;; Formato inválido
(validation-errors "ABCDEFGHIJK")  ; => [...]

;; Número muito curto
(validation-errors "12345678")  ; => ["Voter ID must have 12 digits after cleaning"]

;; Valores nulos/vazios
(validation-errors nil)   ; => [...]
(validation-errors "")    ; => [...]

;; Dígitos repetidos
(validation-errors "00000000000000")  ; => ["All digits cannot be the same"]

;; UF inválido (>28)
(validation-errors "123456789099")  ; => ["Invalid UF code"]

;; Dígitos verificadores inválidos
(validation-errors "123456789999")  ; => [...]
```

## Casos de Uso

### 1. Validação em Cadastro Eleitoral

```clojure
(defn register-voter [voter-info]
  (let [{:keys [name titulo-eleitoral]} voter-info
        clean-id (remove-symbols titulo-eleitoral)]
    (if (is-valid? clean-id)
      {:status :success :voter {:name name :titulo clean-id}}
      {:status :error :errors (validation-errors clean-id)})))

(register-voter {:name "João Silva" :titulo-eleitoral "1234 5678 9035"})
; => {:status :success :voter {...}}
```

### 2. Validação com Extração de Estado

```clojure
(defn validate-and-get-state [titulo-eleitoral]
  (let [clean (remove-symbols titulo-eleitoral)]
    (if (is-valid? clean)
      {:valid? true :voter-id clean :uf-code (get-uf-code clean)}
      {:valid? false :errors (validation-errors clean)})))

(validate-and-get-state "1234 5678 9035")
; => {:valid? true :voter-id "123456789035" :uf-code "35"}
```

### 3. Validação em Lote de Votantes

```clojure
(defn validate-voters [voters]
  (map (fn [voter]
         (let [clean-id (remove-symbols (:titulo voter))]
           (assoc voter 
                  :titulo-valid? (is-valid? clean-id)
                  :titulo-normalized clean-id)))
       voters))

(validate-voters 
  [{:name "João" :titulo "1234 5678 9035"}
   {:name "Maria" :titulo "invalid"}
   {:name "Pedro" :titulo "2345 6789 0146"}])
; => [{:titulo-valid? true ...} ...]
```

### 4. Filtragem por Estado

```clojure
(defn filter-voters-by-state [voters target-state]
  (filter (fn [voter]
            (let [clean-id (remove-symbols (:titulo voter))
                  uf (get-uf-code clean-id)
                  uf-code (Integer/parseInt uf)]
              (= uf-code target-state)))
          voters))

(filter-voters-by-state voters-list 35)  ; São Paulo
; => [voters from SP]
```

### 5. Geração de Dados de Teste

```clojure
(defn generate-test-voters [count state-code]
  (vec (for [_ (range count)]
         {:voter-id (generate {:uf-code state-code})
          :state state-code
          :generated-at (java.time.Instant/now)})))

(generate-test-voters 5 35)
; => [{:voter-id "123456789035" :state 35 ...}
;     {:voter-id "234567890146" :state 35 ...}
;     ...]
```

### 6. Integração com Sistema de Votação

```clojure
(defn authenticate-voter [titulo-eleitoral password]
  (let [clean-id (remove-symbols titulo-eleitoral)]
    (if (is-valid? clean-id)
      {:status :valid 
       :voter-id clean-id
       :uf-code (get-uf-code clean-id)
       :ready-to-vote true}
      {:status :invalid 
       :errors (validation-errors clean-id)
       :ready-to-vote false})))

(authenticate-voter "1234 5678 9035" "password")
; => {:status :valid :voter-id "123456789035" :uf-code "35" :ready-to-vote true}
```

## Códigos de Estado (UF)

| Código | Estado | Código | Estado |
|--------|--------|--------|--------|
| 01 | DF | 15 | PR |
| 02 | AC | 16 | PE |
| 03 | AL | 17 | PI |
| 04 | AP | 18 | RJ |
| 05 | AM | 19 | RN |
| 06 | BA | 20 | RO |
| 07 | CE | 21 | RR |
| 08 | ES | 22 | RS |
| 09 | GO | 23 | SC |
| 10 | MA | 24 | SE |
| 11 | MG | 25 | SP |
| 12 | MT | 26 | TO |
| 13 | MS | 27 | Exterior |
| 14 | PA | 28 | Exterior |

## Formatos Aceitos

| Formato | Exemplo | Válido |
|---------|---------|--------|
| Sem formatação | 123456789035 | ✅ |
| Com espaços | 1234 5678 9035 | ✅ |
| Com hífens | 1234-5678-9035 | ✅ |
| Misto | 1234 5678-9035 | ✅ |
| 8 dígitos | 12345678 | ❌ |
| 11 dígitos | 12345678901 | ❌ |
| UF inválido (>28) | 123456789099 | ❌ |

## Observações Importantes

⚠️ **Este módulo valida ESTRUTURA e MATEMÁTICA, NÃO verifica se o Título Eleitoral existe no registro oficial do TSE (Tribunal Superior Eleitoral).**

Para validação oficial com TSE, você precisa integrar com APIs do TSE ou banco de dados oficial.

## Ver Também

- [CNH](cnh.md) - Carteira Nacional de Habilitação
- [RENAVAM](renavam.md) - Registro Nacional de Veículo Automotor
- [CPF](cpf.md) - Validação de CPF
- [Estados](states.md) - Informações de estados brasileiros
