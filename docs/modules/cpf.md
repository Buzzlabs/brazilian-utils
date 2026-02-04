# CPF - Cadastro de Pessoa Física

Módulo para validação e geração de CPF (Cadastro de Pessoa Física), documento de identificação pessoal brasileiro.

## Visão Geral

O CPF é um número de 11 dígitos que identifica uma pessoa física no Brasil. Este módulo fornece funções para:

- Validar CPF com ou sem formatação
- Formatar CPF em padrão brasileiro
- Gerar CPF válido aleatoriamente
- Obter informações sobre erros de validação

## Algoritmo

O CPF utiliza um algoritmo de validação com 2 dígitos verificadores calculados através do **módulo 11**:

1. **Primeiro dígito verificador**: 
   - Multiplica os 9 primeiros dígitos por [10, 9, 8, 7, 6, 5, 4, 3, 2]
   - Soma todos os produtos
   - Calcula o resto da divisão por 11
   - Se resto < 2, dígito = 0; senão dígito = 11 - resto

2. **Segundo dígito verificador**:
   - Multiplica os 10 dígitos (9 + 1º verificador) por [11, 10, 9, 8, 7, 6, 5, 4, 3, 2]
   - Soma todos os produtos
   - Calcula o resto da divisão por 11
   - Se resto < 2, dígito = 0; senão dígito = 11 - resto

## Funções Principais

### is-valid?

```clojure
(cpf/is-valid? cpf-string)
```

Valida um CPF com ou sem formatação.

**Argumentos:**
- `cpf-string` (string): CPF a validar, com ou sem formatação

**Retorna:**
- `true` se válido, `false` caso contrário

**Exemplos:**
```clojure
(cpf/is-valid? "123.456.789-09")  ; => true/false
(cpf/is-valid? "12345678909")     ; => true/false
(cpf/is-valid? "111.111.111-11")  ; => false (sequência repetida)
```

**Notas:**
- Valida tanto CPF formatado quanto não-formatado
- Rejeita CPFs com todos os dígitos iguais (ex: 111.111.111-11)
- Remove automaticamente caracteres de formatação antes de validar

### format-cpf

```clojure
(cpf/format-cpf cpf-string)
```

Formata um CPF no padrão brasileiro XXX.XXX.XXX-XX.

**Argumentos:**
- `cpf-string` (string): CPF a formatar (com ou sem formatação)

**Retorna:**
- String com CPF formatado como XXX.XXX.XXX-XX

**Exemplos:**
```clojure
(cpf/format-cpf "12345678909")     ; => "123.456.789-09"
(cpf/format-cpf "123.456.789-09")  ; => "123.456.789-09"
```

### generate

```clojure
(cpf/generate)
(cpf/generate {:uf-code :SP})
```

Gera um CPF válido aleatoriamente.

**Argumentos:**
- `:uf-code` (opcional, keyword): Código do estado (UF) que define o 9º dígito
  - Válidos: `:AC`, `:AL`, `:AP`, `:AM`, `:BA`, `:CE`, `:DF`, `:ES`, `:GO`, `:MA`, `:MT`, `:MS`, `:MG`, `:PA`, `:PB`, `:PR`, `:PE`, `:PI`, `:RJ`, `:RN`, `:RS`, `:RO`, `:RR`, `:SC`, `:SP`, `:SE`, `:TO`

**Retorna:**
- String com CPF válido de 11 dígitos (sem formatação)

**Exemplos:**
```clojure
(cpf/generate)                      ; => "12345678909" (aleatório)
(cpf/generate {:uf-code :SP})       ; => CPF com código :SP (8)
(cpf/generate {:uf-code :RJ})       ; => CPF com código :RJ (5)
(cpf/generate {:uf-code :MG})       ; => CPF com código :MG (1)
```

**Códigos UF:**
- SP=8, RJ=5, BA=1, MG=1, RS=4, PR=6, CE=7, PA=9, PE=2, SC=3, GO=2, MA=3, PB=1, RN=0, AL=8, ES=3, PI=2, RO=1, AC=0, AM=2, AP=3, DF=9, MS=2, MT=3, RR=3, SE=0, TO=0

### validation-errors

```clojure
(cpf/validation-errors cpf-string)
```

Retorna uma lista de erros de validação do CPF.

**Argumentos:**
- `cpf-string` (string): CPF a validar

**Retorna:**
- Vector com lista de erros (vazio se válido)

**Exemplos:**
```clojure
(cpf/validation-errors "123.456.789-09")  ; => [] ou ["Invalid check digit"]
(cpf/validation-errors "123")             ; => ["Invalid length"]
(cpf/validation-errors "111.111.111-11")  ; => ["CPF with all equal digits"]
```

## Recursos

- ✅ Validação com ou sem formatação
- ✅ Formatação automática
- ✅ Geração de CPF válido aleatório
- ✅ Suporte a códigos de UF para geração
- ✅ Rejeição de CPFs com dígitos repetidos
- ✅ Mensagens de erro detalhadas
- ✅ Cross-platform (Clojure & ClojureScript)

## Tratamento de Erros

```clojure
(cpf/validation-errors "123")
; => ["Invalid length"]

(cpf/validation-errors "123.456.789-00")
; => ["Invalid check digit"]

(cpf/validation-errors "111.111.111-11")
; => ["CPF with all equal digits"]
```

## Casos de Uso

### Validar entrada de usuário

```clojure
(defn validate-cpf-input [cpf-input]
  (if (cpf/is-valid? cpf-input)
    {:status :valid :cpf (cpf/format-cpf cpf-input)}
    {:status :invalid :errors (cpf/validation-errors cpf-input)}))

(validate-cpf-input "123.456.789-09")
; => {:status :valid :cpf "123.456.789-09"} ou
; => {:status :invalid :errors ["Invalid check digit"]}
```

### Gerar dados de teste

```clojure
; Gerar 10 CPFs válidos para testes
(def test-cpfs
  (repeatedly 10 #(cpf/format-cpf (cpf/generate))))

; Gerar CPFs de um estado específico
(def sp-cpfs
  (repeatedly 5 #(cpf/generate {:uf-code :SP})))
```

### Processamento em lote

```clojure
(defn validate-cpf-batch [cpf-strings]
  (reduce (fn [acc cpf]
            (assoc acc cpf (cpf/is-valid? cpf)))
          {}
          cpf-strings))

(validate-cpf-batch ["123.456.789-09" "111.111.111-11" "12345678909"])
; => {"123.456.789-09" true 
;     "111.111.111-11" false 
;     "12345678909" true/false}
```


## Ver Também

- [CNPJ](cnpj.md) - Para validação de CNPJ (documentos empresariais)
- [Inscrição Estadual](inscricao_estadual.md) - Para IE por estado
