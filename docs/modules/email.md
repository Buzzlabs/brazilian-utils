# Email - Validação de Endereços de E-mail

Módulo para validação de endereços de e-mail com suporte a regras rigorosas de RFC 5321/5322 e feedback detalhado sobre erros de validação.

## Visão Geral

O módulo `email` fornece validação abrangente de endereços de e-mail, com suporte a:
- Validação de estrutura e formato
- Detecção de múltiplos tipos de erro
- Feedback detalhado sobre problemas encontrados
- Conformidade com padrões internacionais (RFC 5321/5322)

**Principais capacidades:**
- ✅ Validação completa de endereço de e-mail
- ✅ Detecção de erros específicos
- ✅ Feedback detalhado e mensagens de erro
- ✅ Suporte a caracteres especiais válidos
- ✅ Verificação de limites de comprimento
- ✅ Tratamento de casos extremos

## Estrutura

Um endereço de e-mail válido segue a estrutura:

```
usuario@dominio.extensao
```

Componentes:
- **Usuário**: Caracteres antes do `@` (mín. 1, máx. 64 caracteres)
- **@**: Separador obrigatório (exatamente um)
- **Domínio**: Nome do servidor (mín. 1 caractere)
- **Extensão**: TLD (Top Level Domain - mín. 2 caracteres)

**Regras:**
- Não pode ser vazio ou branco
- Deve conter exatamente um símbolo `@`
- Usuário não pode ser vazio
- Domínio não pode ser vazio
- Extensão deve ter pelo menos 2 caracteres

## Funções Principais

### `is-valid?`

Valida se um valor é um endereço de e-mail bem formado.

**Assinatura:**
```clojure
(is-valid? email)
```

**Argumentos:**
- `email` - Valor a validar como endereço de e-mail

**Retorna:** Boolean - `true` se for um e-mail válido, `false` caso contrário

**Exemplos:**
```clojure
(is-valid? "user@example.com")          ; => true
(is-valid? "john.doe@company.co.uk")    ; => true
(is-valid? "alice+tag@domain.org")      ; => true
(is-valid? "")                          ; => false
(is-valid? nil)                         ; => false
(is-valid? "invalid.email")             ; => false (sem @)
(is-valid? "user@@example.com")         ; => false (@ duplicado)
(is-valid? "user@.com")                 ; => false (sem domínio)
(is-valid? "user@example")              ; => false (sem TLD)
```

### `validation-errors`

Valida um e-mail e retorna informações detalhadas sobre erros.

**Assinatura:**
```clojure
(validation-errors email)
```

**Argumentos:**
- `email` - Valor a validar como endereço de e-mail

**Retorna:** Vetor de strings com mensagens de erro (vazio se válido)

**Exemplos:**
```clojure
(validation-errors "user@example.com")
; => []

(validation-errors "")
; => ["Email cannot be blank"]

(validation-errors nil)
; => ["should be a string"]

(validation-errors "user@@example.com")
; => ["Email should have only one @"]

(validation-errors "userexample.com")
; => ["Email should have an @"]

(validation-errors "user@.com")
; => ["Email domain is invalid"]

(validation-errors "user@example")
; => ["Email domain must have an extension"]
```

## Recursos

- ✅ Validação rigorosa de formato de e-mail
- ✅ Detecção de múltiplos tipos de erro
- ✅ Mensagens de erro descritivas e específicas
- ✅ Suporte a caracteres especiais válidos (., +, -, _)
- ✅ Validação de limites de comprimento
- ✅ Case-insensitive para domínios
- ✅ Tratamento seguro de valores nulos/vazios
- ✅ Conformidade com RFC 5321/5322

## Tratamento de Erros

O módulo fornece feedback detalhado sobre problemas:

```clojure
;; E-mail vazio
(validation-errors "")
; => ["Email cannot be blank"]

;; Tipo incorreto
(validation-errors 123)
; => ["should be a string"]

;; Sem @ ou @ duplicado
(validation-errors "userexample.com")
; => ["Email should have an @"]

(validation-errors "user@@example.com")
; => ["Email should have only one @"]

;; Domínio inválido
(validation-errors "user@")
; => ["Email domain is invalid"]

(validation-errors "user@.com")
; => ["Email domain is invalid"]

;; Sem extensão (TLD)
(validation-errors "user@example")
; => ["Email domain must have an extension"]

;; Usuário muito longo (>64 caracteres)
(validation-errors (str (apply str (repeat 65 "a")) "@example.com"))
; => ["Email local part must be less than 64 characters"]
```

## Casos de Uso

### 1. Validação em Formulários de Registro

```clojure
(defn validate-email-field [email-input]
  (if (is-valid? email-input)
    {:status :valid :email email-input}
    {:status :invalid 
     :errors (validation-errors email-input)
     :input email-input}))

(validate-email-field "john@example.com")
; => {:status :valid :email "john@example.com"}

(validate-email-field "invalid")
; => {:status :invalid :errors [...] :input "invalid"}
```

### 2. Filtragem de E-mails Válidos em Lote

```clojure
(defn filter-valid-emails [email-list]
  (filter is-valid? email-list))

(filter-valid-emails 
  ["john@example.com"
   "invalid.email"
   "alice@domain.co.uk"
   nil
   "bob@site.org"])
; => ["john@example.com" "alice@domain.co.uk" "bob@site.org"]
```

### 3. Validação com Feedback ao Usuário

```clojure
(defn get-validation-message [email]
  (if (is-valid? email)
    "E-mail válido!"
    (let [errors (validation-errors email)
          first-error (first errors)]
      (str "Erro: " first-error))))

(get-validation-message "user@example.com")
; => "E-mail válido!"

(get-validation-message "userexample")
; => "Erro: Email should have an @"
```

### 4. Normalização de E-mail com Validação

```clojure
(defn normalize-email [email]
  (let [trimmed (clojure.string/trim email)
        lowercased (clojure.string/lower-case trimmed)]
    (when (is-valid? lowercased)
      lowercased)))

(normalize-email "  User@Example.COM  ")
; => "user@example.com"

(normalize-email "  invalid  ")
; => nil
```

### 5. Processamento de Lista de Contatos

```clojure
(defn process-contacts [contacts]
  (let [grouped (group-by 
                  (fn [contact]
                    (if (is-valid? (:email contact))
                      :valid
                      :invalid))
                  contacts)]
    {:valid-contacts (get grouped :valid [])
     :invalid-contacts (get grouped :invalid [])
     :error-details (into {}
                      (map (fn [c] 
                             [(:email c) 
                              (validation-errors (:email c))])
                           (get grouped :invalid [])))}))

(process-contacts 
  [{:name "João" :email "joao@example.com"}
   {:name "Maria" :email "invalid"}
   {:name "Pedro" :email "pedro@domain.org"}])
; => {:valid-contacts [...] 
;     :invalid-contacts [...] 
;     :error-details {...}}
```

### 6. API REST com Validação

```clojure
(defn validate-email-endpoint [email]
  (if (is-valid? email)
    {:status 200
     :body {:valid true :email email}}
    {:status 400
     :body {:valid false 
            :errors (validation-errors email)}}))

(validate-email-endpoint "user@example.com")
; => {:status 200 :body {:valid true :email "user@example.com"}}

(validate-email-endpoint "bad-email")
; => {:status 400 :body {:valid false :errors [...]}}
```

## Padrões de E-mail Válidos

| Padrão | Exemplo | Válido |
|--------|---------|--------|
| Simples | user@domain.com | ✅ |
| Com ponto | john.doe@company.com | ✅ |
| Com + (alias) | user+tag@domain.com | ✅ |
| Com hífen | john-smith@domain.co.uk | ✅ |
| Com underscore | user_name@domain.org | ✅ |
| Vazio | "" | ❌ |
| Sem @ | user.domain.com | ❌ |
| @ duplicado | user@@domain.com | ❌ |
| Sem domínio | user@ | ❌ |
| Sem TLD | user@domain | ❌ |

## Ver Também

- [Capitalize](capitalize.md) - Formatação de nomes e textos
- [Telefone](phone.md) - Validação de números de telefone
- [CPF](cpf.md) - Validação de CPF
- [CNPJ](cnpj.md) - Validação de CNPJ
