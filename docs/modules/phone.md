# Telefone - Validação de Números de Telefone

Módulo para validação de números de telefone brasileiro (celular e fixo).

## Visão Geral

Este módulo fornece funções para:

- Validar telefone celular
- Validar telefone fixo
- Validar telefone genérico
- Formatar telefone no padrão brasileiro
- Obter informações sobre erros de validação

## Estrutura do Telefone Brasileiro

### Celular

- **11 dígitos** total
- Formato: (XX) 9XXXX-XXXX
- Estrutura:
  - 2 dígitos: DDD (Código de Área)
  - 1 dígito: 9 (identificador de celular)
  - 4 dígitos: Primeira parte do número
  - 4 dígitos: Segunda parte do número
- Exemplo: (11) 99999-9999

### Fixo (Landline)

- **10 dígitos** total
- Formato: (XX) XXXX-XXXX
- Estrutura:
  - 2 dígitos: DDD (Código de Área)
  - 4 dígitos: Primeira parte do número
  - 4 dígitos: Segunda parte do número
- Exemplo: (11) 3333-4444

## DDDs Válidos

DDDs são códigos de área brasileiros de 2 dígitos. Exemplos:

- SP (São Paulo): 11, 12, 13, 14, 15, 16, 17, 18, 19
- RJ (Rio de Janeiro): 21, 22, 24
- MG (Minas Gerais): 31, 32, 33, 34, 35, 37, 38
- BA (Bahia): 71, 73, 74, 75, 77
- RS (Rio Grande do Sul): 51, 53, 54, 55
- E muitos mais...

## Funções Principais

### is-valid?

```clojure
(phone/is-valid? phone-string)
```

Valida um telefone (celular ou fixo) com ou sem formatação.

**Argumentos:**
- `phone-string` (string): Telefone a validar, com ou sem formatação

**Retorna:**
- `true` se válido, `false` caso contrário

**Exemplos:**
```clojure
(phone/is-valid? "(11) 99999-9999")  ; => true (celular)
(phone/is-valid? "(11) 3333-4444")   ; => true (fixo)
(phone/is-valid? "11999999999")      ; => true (sem formatação)
(phone/is-valid? "1133334444")       ; => true (sem formatação)
(phone/is-valid? "(11) 1234-5678")   ; => false (DDD inválido)
(phone/is-valid? "123")              ; => false (muito curto)
```

### is-valid-mobile?

```clojure
(phone/is-valid-mobile? phone-string)
```

Valida especificamente um telefone celular.

**Argumentos:**
- `phone-string` (string): Celular a validar

**Retorna:**
- `true` se for um celular válido, `false` caso contrário

**Exemplos:**
```clojure
(phone/is-valid-mobile? "11999999999")      ; => true
(phone/is-valid-mobile? "(11) 99999-9999")  ; => true
(phone/is-valid-mobile? "1133334444")       ; => false (é fixo)
(phone/is-valid-mobile? "(11) 3333-4444")   ; => false (é fixo)
```

### is-valid-landline?

```clojure
(phone/is-valid-landline? phone-string)
```

Valida especificamente um telefone fixo (landline).

**Argumentos:**
- `phone-string` (string): Fixo a validar

**Retorna:**
- `true` se for um telefone fixo válido, `false` caso contrário

**Exemplos:**
```clojure
(phone/is-valid-landline? "1133334444")       ; => true
(phone/is-valid-landline? "(11) 3333-4444")   ; => true
(phone/is-valid-landline? "11999999999")      ; => false (é celular)
(phone/is-valid-landline? "(11) 99999-9999")  ; => false (é celular)
```

### format-phone

```clojure
(phone/format-phone phone-string)
```

Formata um telefone no padrão brasileiro.

**Argumentos:**
- `phone-string` (string): Telefone a formatar (com ou sem formatação)

**Retorna:**
- String com telefone formatado como (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX
- `nil` se telefone inválido

**Exemplos:**
```clojure
(phone/format-phone "11999999999")      ; => "(11) 99999-9999"
(phone/format-phone "1133334444")       ; => "(11) 3333-4444"
(phone/format-phone "(11) 99999-9999")  ; => "(11) 99999-9999"
(phone/format-phone "123")              ; => nil
```

### validation-errors

```clojure
(phone/validation-errors phone-string)
```

Retorna uma lista de erros de validação do telefone.

**Argumentos:**
- `phone-string` (string): Telefone a validar

**Retorna:**
- Vector com lista de erros (vazio se válido)

**Exemplos:**
```clojure
(phone/validation-errors "(11) 99999-9999")  ; => []
(phone/validation-errors "123")              ; => ["Invalid length" "Invalid area code"]
(phone/validation-errors "(99) 99999-9999")  ; => ["Invalid area code"] (DDD não existe)
```

## Recursos

- ✅ Validação de celular
- ✅ Validação de telefone fixo
- ✅ Validação genérica (celular ou fixo)
- ✅ Formatação automática
- ✅ Suporte a múltiplos formatos de entrada
- ✅ Validação de DDD (Código de Área)
- ✅ Mensagens de erro detalhadas
- ✅ Cross-platform (Clojure & ClojureScript)

## Tratamento de Erros

```clojure
(phone/validation-errors "123")
; => ["Invalid length"]

(phone/validation-errors "(99) 99999-9999")
; => ["Invalid area code"]

(phone/validation-errors "(11) 1234-5678")
; => ["Invalid phone number"]
```

## Casos de Uso

### Validar entrada de telefone

```clojure
(defn validate-phone-input [phone-input]
  (if (phone/is-valid? phone-input)
    {:status :valid 
     :phone (phone/format-phone phone-input)
     :type (if (phone/is-valid-mobile? phone-input) :mobile :landline)}
    {:status :invalid :errors (phone/validation-errors phone-input)}))

(validate-phone-input "11999999999")
; => {:status :valid :phone "(11) 99999-9999" :type :mobile}

(validate-phone-input "1133334444")
; => {:status :valid :phone "(11) 3333-4444" :type :landline}
```

### Separar celulares de fixos

```clojure
(defn classify-phones [phone-list]
  (reduce (fn [acc phone]
            (cond
              (phone/is-valid-mobile? phone)
              (update acc :mobiles conj (phone/format-phone phone))
              
              (phone/is-valid-landline? phone)
              (update acc :landlines conj (phone/format-phone phone))
              
              :else
              (update acc :invalid conj phone)))
          {:mobiles [] :landlines [] :invalid []}
          phone-list))

(classify-phones ["11999999999" "1133334444" "abc" "(21) 98765-4321"])
; => {:mobiles ["(11) 99999-9999" "(21) 98765-4321"] 
;     :landlines ["(11) 3333-4444"] 
;     :invalid ["abc"]}
```

### Validação em formulário

```clojure
(defn validate-contact-form [form-data]
  (let [phone-validation (phone/validation-errors (:phone form-data))]
    (if (empty? phone-validation)
      {:status :valid :data (assoc form-data :phone (phone/format-phone (:phone form-data)))}
      {:status :invalid :errors {:phone phone-validation}})))
```

### Enviar SMS (exemplo)

```clojure
(defn send-sms [phone-number message]
  (if (phone/is-valid-mobile? phone-number)
    {:status :sending
     :to (phone/format-phone phone-number)
     :message message}
    {:status :error
     :reason "Invalid mobile phone number"
     :errors (phone/validation-errors phone-number)}))
```

## DDDs por Estado

| Estado | DDDs |
|--------|------|
| SP | 11, 12, 13, 14, 15, 16, 17, 18, 19 |
| RJ | 21, 22, 24 |
| MG | 31, 32, 33, 34, 35, 37, 38 |
| BA | 71, 73, 74, 75, 77 |
| RS | 51, 53, 54, 55 |
| SC | 47, 48, 49 |
| PR | 41, 42, 43, 44, 45, 46 |
| PE | 81, 87 |
| CE | 85, 88 |
| PA | 91, 93, 94 |
| GO | 62, 64 |
| DF | 61 |


## Ver Também

- [Email](email.md) - Para validação de email
- [Estados](states.md) - Para DDDs por estado
