# Processo Jurídico - Validação de Números de Processo

Módulo para validação, formatação e parsing de números de processo jurídico brasileiros.

## Visão Geral

Este módulo fornece funções para:

- Validar número de processo jurídico
- Formatar número de processo no padrão brasileiro
- Extrair informações de um número de processo
- Obter informações sobre erros de validação

## Estrutura do Número de Processo

Um número de processo jurídico brasileiro possui **20 dígitos** no total, estruturados da seguinte forma:

### Formato Bruto
**NNNNNNNDDAAAAJTTOOOO**

- **7 dígitos (NNNNNNN)**: Número sequencial
- **2 dígitos (DD)**: Dígitos verificadores (MOD 97-10)
- **4 dígitos (AAAA)**: Ano de ajuizamento
- **1 dígito (J)**: Segmento de Justiça
- **2 dígitos (TT)**: Tribunal
- **4 dígitos (OOOO)**: Origem

### Formato Formatado
**NNNNNNN.DD.AAAA.J.TT.OOOO**

Exemplo:
- Bruto: 00012345678901234567
- Formatado: 0001234-56.7890.1.23.4567

## Segmentos de Justiça

| Código | Segmento |
|--------|----------|
| 1 | Justiça Estadual |
| 2 | Justiça Federal |
| 3 | Justiça do Trabalho |
| 4 | Justiça Eleitoral |
| 5 | Justiça Militar |
| 6 | Tribunais Superiores |
| 7 | Justiça de Paz |
| 8 | Estrutura Judiciária Militar |
| 9 | Estrutura Complementar |

## Algoritmo MOD 97-10

O número de processo utiliza o algoritmo **MOD 97-10** para os dígitos verificadores:

1. Pega os primeiros 7 dígitos (número sequencial) + 4 dígitos de ano + 1 dígito de segmento + 2 de tribunal + 4 de origem = 18 dígitos
2. Calcula: resto = número mod 97
3. Dígito verificador = 98 - resto
4. Resultado deve ser um número de 2 dígitos (com padding de zeros se necessário)

## Funções Principais

### is-valid?

```clojure
(processo/is-valid? process-number)
```

Valida um número de processo jurídico.

**Argumentos:**
- `process-number` (string): Número de processo a validar (com ou sem formatação)

**Retorna:**
- `true` se válido, `false` caso contrário

**Exemplos:**
```clojure
(processo/is-valid? "0001234-56.7890.1.23.4567")  ; => true/false
(processo/is-valid? "00012345678901234567")       ; => true/false
(processo/is-valid? "0001234-56.7890.1.23.4568")  ; => false (verificador inválido)
(processo/is-valid? "123")                        ; => false (muito curto)
```

### format-processo

```clojure
(processo/format-processo process-number)
```

Formata um número de processo jurídico no padrão brasileiro.

**Argumentos:**
- `process-number` (string): Número de processo a formatar (com ou sem formatação)

**Retorna:**
- String com processo formatado como NNNNNNN.DD.AAAA.J.TT.OOOO

**Exemplos:**
```clojure
(processo/format-processo "00012345678901234567")
; => "0001234-56.7890.1.23.4567"

(processo/format-processo "0001234-56.7890.1.23.4567")
; => "0001234-56.7890.1.23.4567"
```

### parse-processo

```clojure
(processo/parse-processo process-number)
```

Extrai informações de um número de processo jurídico.

**Argumentos:**
- `process-number` (string): Número de processo a analisar (com ou sem formatação)

**Retorna:**
- Map com informações extraídas:
  ```clojure
  {:sequential "0001234"           ; 7 dígitos
   :verification-digits "56"        ; 2 dígitos
   :year "7890"                     ; 4 dígitos (ano)
   :segment "1"                     ; 1 dígito (justiça)
   :court "23"                      ; 2 dígitos (tribunal)
   :origin "4567"}                  ; 4 dígitos (origem)
  ```
- `nil` se processo inválido

**Exemplos:**
```clojure
(processo/parse-processo "0001234-56.7890.1.23.4567")
; => {:sequential "0001234"
;     :verification-digits "56"
;     :year "7890"
;     :segment "1"
;     :court "23"
;     :origin "4567"}

(processo/parse-processo "INVALID")
; => nil
```

### validation-errors

```clojure
(processo/validation-errors process-number)
```

Retorna uma lista de erros de validação do processo.

**Argumentos:**
- `process-number` (string): Número de processo a validar

**Retorna:**
- Vector com lista de erros (vazio se válido)

**Exemplos:**
```clojure
(processo/validation-errors "0001234-56.7890.1.23.4567")  ; => []
(processo/validation-errors "123")                        ; => ["Invalid length"]
(processo/validation-errors "0001234-56.7890.1.23.4568")  ; => ["Invalid verification digits"]
```

## Recursos

- ✅ Validação com algoritmo MOD 97-10
- ✅ Formatação automática
- ✅ Parsing e extração de informações
- ✅ Suporte a múltiplos formatos (com e sem formatação)
- ✅ Validação de estrutura completa
- ✅ Mensagens de erro detalhadas
- ✅ Cross-platform (Clojure & ClojureScript)

## Tratamento de Erros

```clojure
(processo/validation-errors "123")
; => ["Invalid length"]

(processo/validation-errors "ABCDEFGHIJKLMNOPQRST")
; => ["Invalid format" "Invalid verification digits"]

(processo/validation-errors "0001234-56.7890.1.23.4568")
; => ["Invalid verification digits"]
```

## Casos de Uso

### Validar entrada de número de processo

```clojure
(defn validate-process-input [process-input]
  (if (processo/is-valid? process-input)
    {:status :valid 
     :process (processo/format-processo process-input)
     :info (processo/parse-processo process-input)}
    {:status :invalid :errors (processo/validation-errors process-input)}))

(validate-process-input "00012345678901234567")
; => {:status :valid 
;     :process "0001234-56.7890.1.23.4567"
;     :info {:sequential "0001234" :verification-digits "56" ...}}
```

### Extrair informações de processo

```clojure
(defn process-info [process-number]
  (if-let [info (processo/parse-processo process-number)]
    {:sequential (:sequential info)
     :year (Integer/parseInt (:year info))
     :segment-type (case (:segment info)
                     "1" :state-court
                     "2" :federal-court
                     "3" :labor-court
                     "4" :electoral-court
                     :unknown)
     :tribunal (:court info)
     :origin (:origin info)
     :formatted (processo/format-processo process-number)}
    nil))

(process-info "0001234-56.7890.1.23.4567")
; => {:sequential "0001234" :year 7890 :segment-type :state-court ...}
```

### Validar em lote

```clojure
(defn validate-process-batch [process-list]
  (reduce (fn [acc process]
            (assoc acc process 
                   {:valid? (processo/is-valid? process)
                    :formatted (when (processo/is-valid? process)
                                 (processo/format-processo process))
                    :errors (processo/validation-errors process)}))
          {}
          process-list))

(validate-process-batch ["0001234-56.7890.1.23.4567" "INVALID" "00012345678901234567"])
```

### Busca por segmento de justiça

```clojure
(defn find-processes-by-segment [process-list segment-code]
  (filter (fn [process]
            (when-let [info (processo/parse-processo process)]
              (= (:segment info) segment-code)))
          process-list))

(find-processes-by-segment ["0001234-56.7890.1.23.4567" "0005678-90.1234.2.56.7890"]
                           "1")  ; Justiça Estadual
```


## Notas Técnicas

### MOD 97-10 em Diferentes Plataformas

O algoritmo MOD 97-10 requer suporte a números grandes (BigInt) para cálculos precisos:

- **Clojure**: Usa naturalmente BigInt quando necessário
- **ClojureScript**: Usa `js/BigInt` ou bibliotecas auxiliares
- A biblioteca abstrai automaticamente essas diferenças

### Performance

Para validação em lote de processos:

```clojure
; Bom: Validar múltiplos processos
(def valid-processes
  (filter processo/is-valid? process-list))

; Bom: Com mapeamento
(def process-info-map
  (reduce (fn [acc p]
            (if (processo/is-valid? p)
              (assoc acc p (processo/parse-processo p))
              acc))
          {}
          process-list))
```

## Ver Também

- [Documentação Oficial do CNJ](https://www.cnj.jus.br/) - Conselho Nacional de Justiça
