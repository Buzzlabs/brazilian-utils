# CNPJ - Cadastro Nacional da Pessoa Jurídica

Módulo para validação e geração de CNPJ (Cadastro Nacional da Pessoa Jurídica), documento de identificação empresarial brasileiro.

## Visão Geral

O CNPJ é um número de 14 dígitos que identifica uma pessoa jurídica (empresa) no Brasil. Este módulo fornece funções para:

- Validar CNPJ numérico (padrão anterior) com ou sem formatação
- Validar CNPJ alfanumérico (novo padrão) 
- Formatar CNPJ em padrão brasileiro
- Gerar CNPJ válido aleatoriamente (numérico e alfanumérico)
- Obter informações sobre erros de validação

## Visão Geral dos Formatos

### CNPJ Numérico (Padrão Anterior)

- 14 dígitos numéricos
- Formato: XX.XXX.XXX/XXXX-XX
- Exemplo: 12.345.678/0001-95
- Estrutura: [NNNNNNNNNNNNNNN]
  - 8 primeiros dígitos: número da empresa
  - 4 dígitos: filial/matriz
  - 2 dígitos: verificadores

### CNPJ Alfanumérico (Novo Padrão)

- 14 caracteres (letras e dígitos)
- Formato: LLNNNNNNNNNNNN
- Exemplo: AB1234567000195
- Estrutura: 2 letras + 12 dígitos
- Suporta A-Z (26 letras) e 0-9 (10 dígitos) = 36 possibilidades

## Algoritmo

O CNPJ numérico utiliza um algoritmo de validação com 2 dígitos verificadores calculados através do **módulo 11**:

1. **Primeiro dígito verificador**:
   - Multiplica os 8 primeiros dígitos + 4 dígitos de filial por [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
   - Soma todos os produtos
   - Calcula o resto da divisão por 11
   - Se resto < 2, dígito = 0; senão dígito = 11 - resto

2. **Segundo dígito verificador**:
   - Multiplica todos os 12 dígitos anteriores + 1º verificador por [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
   - Soma todos os produtos
   - Calcula o resto da divisão por 11
   - Se resto < 2, dígito = 0; senão dígito = 11 - resto

## Funções Principais

### is-valid?

```clojure
(cnpj/is-valid? cnpj-string)
```

Valida um CNPJ numérico com ou sem formatação.

**Argumentos:**
- `cnpj-string` (string): CNPJ a validar, com ou sem formatação

**Retorna:**
- `true` se válido, `false` caso contrário

**Exemplos:**
```clojure
(cnpj/is-valid? "12.345.678/0001-95")  ; => true/false
(cnpj/is-valid? "12345678000195")     ; => true/false
(cnpj/is-valid? "11.222.333/0001-81")  ; => false (dígitos verificadores inválidos)
```

### format-cnpj

```clojure
(cnpj/format-cnpj cnpj-string)
```

Formata um CNPJ no padrão brasileiro XX.XXX.XXX/XXXX-XX.

**Argumentos:**
- `cnpj-string` (string): CNPJ a formatar (com ou sem formatação)

**Retorna:**
- String com CNPJ formatado como XX.XXX.XXX/XXXX-XX

**Exemplos:**
```clojure
(cnpj/format-cnpj "12345678000195")   ; => "12.345.678/0001-95"
(cnpj/format-cnpj "12.345.678/0001-95") ; => "12.345.678/0001-95"
```

### generate

```clojure
(cnpj/generate)
```

Gera um CNPJ numérico válido aleatoriamente.

**Retorna:**
- String com CNPJ válido de 14 dígitos (sem formatação)

**Exemplos:**
```clojure
(cnpj/generate)  ; => "12345678000195" (aleatório válido)
```

### generate-alfanumeric

```clojure
(cnpj/generate-alfanumeric)
```

Gera um CNPJ alfanumérico válido aleatoriamente (novo padrão).

**Retorna:**
- String com CNPJ alfanumérico válido: 2 letras + 12 dígitos

**Exemplos:**
```clojure
(cnpj/generate-alfanumeric)  ; => "AB1234567000195" (aleatório válido)
(cnpj/generate-alfanumeric)  ; => "XY9876543210654"
```

### is-valid-alfanumeric?

```clojure
(cnpj/is-valid-alfanumeric? cnpj-string)
```

Valida um CNPJ alfanumérico (novo padrão).

**Argumentos:**
- `cnpj-string` (string): CNPJ alfanumérico a validar

**Retorna:**
- `true` se válido, `false` caso contrário

**Exemplos:**
```clojure
(cnpj/is-valid-alfanumeric? "AB1234567000195")  ; => true/false
(cnpj/is-valid-alfanumeric? "XY9876543210654")  ; => true/false
(cnpj/is-valid-alfanumeric? "12345678000195")   ; => false (não é alfanumérico)
```

### validation-errors

```clojure
(cnpj/validation-errors cnpj-string)
```

Retorna uma lista de erros de validação do CNPJ.

**Argumentos:**
- `cnpj-string` (string): CNPJ a validar

**Retorna:**
- Vector com lista de erros (vazio se válido)

**Exemplos:**
```clojure
(cnpj/validation-errors "12.345.678/0001-95")  ; => [] ou ["Invalid check digit"]
(cnpj/validation-errors "123")                 ; => ["Invalid length"]
(cnpj/validation-errors "11.222.333/0001-81")  ; => ["Invalid check digit"]
```

## Recursos

- ✅ Validação de CNPJ numérico com ou sem formatação
- ✅ Validação de CNPJ alfanumérico (novo padrão)
- ✅ Formatação automática
- ✅ Geração de CNPJ numérico válido aleatório
- ✅ Geração de CNPJ alfanumérico válido aleatório
- ✅ Mensagens de erro detalhadas
- ✅ Cross-platform (Clojure & ClojureScript)

## Tratamento de Erros

```clojure
(cnpj/validation-errors "123")
; => ["Invalid length"]

(cnpj/validation-errors "12.345.678/0001-00")
; => ["Invalid check digit"]
```

## Casos de Uso

### Validar entrada de usuário

```clojure
(defn validate-cnpj-input [cnpj-input]
  (if (cnpj/is-valid? cnpj-input)
    {:status :valid :cnpj (cnpj/format-cnpj cnpj-input)}
    {:status :invalid :errors (cnpj/validation-errors cnpj-input)}))

(validate-cnpj-input "12.345.678/0001-95")
; => {:status :valid :cnpj "12.345.678/0001-95"} ou
; => {:status :invalid :errors ["Invalid check digit"]}
```

### Detectar tipo de CNPJ

```clojure
(defn cnpj-type [cnpj-string]
  (cond
    (cnpj/is-valid? cnpj-string) :numeric
    (cnpj/is-valid-alfanumeric? cnpj-string) :alfanumeric
    :else :invalid))

(cnpj-type "12.345.678/0001-95")    ; => :numeric
(cnpj-type "AB1234567000195")       ; => :alfanumeric
(cnpj-type "INVALID")               ; => :invalid
```

### Gerar dados de teste

```clojure
; Gerar 10 CNPJs numéricos válidos
(def test-cnpjs
  (repeatedly 10 #(cnpj/format-cnpj (cnpj/generate))))

; Gerar 10 CNPJs alfanuméricos válidos
(def alfanum-cnpjs
  (repeatedly 10 #(cnpj/generate-alfanumeric)))
```


## Ver Também

- [CPF](cpf.md) - Para validação de CPF (documentos pessoais)
- [Inscrição Estadual](inscricao_estadual.md) - Para IE por estado
- [API Reference](../api-reference.md) - Referência completa de todas as funções
