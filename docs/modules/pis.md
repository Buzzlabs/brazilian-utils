# PIS - Programa de Integração Social

Módulo para validação de PIS (Programa de Integração Social), incluindo verificação de dígito verificador e remoção de caracteres de formatação.

## Visão Geral

O módulo `pis` fornece validação completa de números PIS brasileiros, com suporte a:
- Validação de números com 11 dígitos
- Verificação de dígito verificador
- Remoção de caracteres de formatação
- Prevenção de padrões inválidos (dígitos repetidos)

**Principais capacidades:**
- ✅ Validação de PIS com 11 dígitos
- ✅ Verificação robusta de dígito verificador
- ✅ Detecção de sequências de dígitos repetidos
- ✅ Suporte a múltiplos formatos de entrada
- ✅ Remoção de caracteres de formatação

## Estrutura

O PIS é um número de 11 dígitos que identifica um trabalhador no programa:

```
XXXXXXXXXXX
```

**Formato com formatação:**
```
XXX.XXXXX.XX-X
```

**Características:**
- Total de 11 dígitos
- Um dígito verificador
- Pode incluir pontos e hífen para formatação
- Emitido pelo Ministério do Trabalho
- Requerido para acesso a benefícios

## Algoritmo de Validação

A validação PIS utiliza módulo 11:

1. Multiplica cada um dos primeiros 10 dígitos pela sequência (3,2,9,8,7,6,5,4,3,2)
2. Calcula o módulo 11 da soma
3. Verifica o 11º dígito como verificador
4. Rejeita sequências de dígitos repetidos (00000000000, 11111111111, etc.)

## Funções Principais

### `is-valid?`

Valida se um número PIS é válido.

**Assinatura:**
```clojure
(is-valid? pis)
```

**Argumentos:**
- `pis` - String com PIS (formatada ou não)

**Retorna:** Boolean - `true` se for válido, `false` caso contrário

**Exemplos:**
```clojure
(is-valid? "120.56874.10-7")    ; => true/false (depende de dígito verificador)
(is-valid? "12056874107")       ; => true/false
(is-valid? "00000000000")       ; => false (dígitos repetidos)
(is-valid? "12345678901")       ; => false (dígito verificador inválido)
(is-valid? "1234567890")        ; => false (10 dígitos)
(is-valid? nil)                 ; => false
(is-valid? "")                  ; => false
```

### `remove-symbols`

Remove todos os caracteres não numéricos de um PIS.

**Assinatura:**
```clojure
(remove-symbols pis)
```

**Argumentos:**
- `pis` - String com PIS (formatada ou não); nil permitido

**Retorna:** String contendo apenas dígitos

**Exemplos:**
```clojure
(remove-symbols "120.56874.10-7")  ; => "12056874107"
(remove-symbols "12056874107")     ; => "12056874107"
(remove-symbols "120 568 741 07")  ; => "12056874107"
(remove-symbols nil)               ; => ""
(remove-symbols "")                ; => ""
```

## Recursos

- ✅ Validação de PIS com 11 dígitos
- ✅ Verificação de dígito verificador via módulo 11
- ✅ Detecção de sequências repetidas (00...0, 11...1, etc.)
- ✅ Suporte a múltiplos formatos
- ✅ Remoção eficiente de caracteres de formatação
- ✅ Tratamento seguro de valores nulos e vazios
- ✅ Case-insensitive para entradas

## Tratamento de Erros

O módulo não lança exceções. Retorna `false` para entradas inválidas:

```clojure
;; Formato inválido
(is-valid? "ABCDEFGHIJK")  ; => false
(is-valid? "12345678")     ; => false (10 dígitos)

;; Valores nulos/vazios
(is-valid? nil)  ; => false
(is-valid? "")   ; => false

;; Dígitos repetidos
(is-valid? "00000000000")  ; => false
(is-valid? "11111111111")  ; => false

;; Dígitos verificadores inválidos
(is-valid? "12345678901")  ; => false (verificador incorreto)

;; Números formatados
(is-valid? "120.56874.10-7")  ; => true/false (depende de algoritmo)
```

## Casos de Uso

### 1. Validação em Cadastro de Funcionário

```clojure
(defn register-employee [employee-info]
  (let [{:keys [name pis]} employee-info
        clean-pis (remove-symbols pis)]
    (if (is-valid? clean-pis)
      {:status :success :employee {:name name :pis clean-pis}}
      {:status :error :message "PIS inválido"})))

(register-employee {:name "João Silva" :pis "120.56874.10-7"})
; => {:status :success :employee {...}}
```

### 2. Normalização de PIS

```clojure
(defn normalize-pis [pis-input]
  (let [cleaned (remove-symbols pis-input)
        trimmed (clojure.string/trim cleaned)]
    (when (is-valid? trimmed)
      trimmed)))

(normalize-pis "  120.56874.10-7  ")
; => "12056874107"

(normalize-pis "invalid")
; => nil
```

### 3. Validação em Lote de Funcionários

```clojure
(defn validate-employees [employees]
  (map (fn [emp]
         (assoc emp 
                :pis-valid? (is-valid? (remove-symbols (:pis emp)))))
       employees))

(validate-employees 
  [{:name "João" :pis "120.56874.10-7"}
   {:name "Maria" :pis "invalid"}
   {:name "Pedro" :pis "130.12345.67-8"}])
; => [{:pis-valid? true} {:pis-valid? false} ...]
```

### 4. Processamento de Folha de Pagamento

```clojure
(defn process-payroll [employees]
  (let [valid-employees (filter 
                          #(is-valid? (remove-symbols (:pis %)))
                          employees)
        invalid-employees (filter 
                            #(not (is-valid? (remove-symbols (:pis %))))
                            employees)]
    {:total (count employees)
     :valid (count valid-employees)
     :invalid (count invalid-employees)
     :valid-employees valid-employees
     :invalid-employees invalid-employees}))

(process-payroll employees-list)
; => {:total 100 :valid 95 :invalid 5 :valid-employees [...] :invalid-employees [...]}
```

### 5. Filtragem de PIS Válidos

```clojure
(defn filter-valid-pis [pis-list]
  (->> pis-list
       (map remove-symbols)
       (filter is-valid?)))

(filter-valid-pis 
  ["120.56874.10-7"
   "invalid"
   "130.12345.67-8"
   nil])
; => ["12056874107" "13012345678"]
```

### 6. Integração com Sistema de FGTS

```clojure
(defn submit-fgts-data [worker-data]
  (let [pis (remove-symbols (:pis worker-data))]
    (if (is-valid? pis)
      {:status :submitted
       :worker {:name (:name worker-data)
                :pis pis
                :fgts-account (:fgts-account worker-data)}}
      {:status :error 
       :message "PIS inválido - não foi possível submeter dados de FGTS"})))

(submit-fgts-data {:name "Ana Silva" :pis "120.56874.10-7" :fgts-account "123456"})
; => {:status :submitted :worker {...}}
```

## Formatos Aceitos

| Formato | Exemplo | Válido |
|---------|---------|--------|
| Sem formatação | 12056874107 | ✅ |
| Com pontos e hífen | 120.56874.10-7 | ✅ |
| Com espaços | 120 568 741 07 | ✅ |
| Parcial | 120 568 74107 | ✅ |
| 10 dígitos | 1205687410 | ❌ |
| 12 dígitos | 120568741070 | ❌ |

## Programas Relacionados

| Programa | Sigla | Uso |
|----------|-------|-----|
| Programa de Integração Social | PIS | Benefícios do trabalhador |
| PASEP | PASEP | Complemento ao PIS |
| FGTS | FGTS | Fundo de Garantia do Tempo de Serviço |
| Poupança | CEF | Conta do FGTS |

## Ver Também

- [CPF](cpf.md) - Validação de CPF
- [CNPJ](cnpj.md) - Validação de CNPJ
- [Inscrição Estadual](inscricao_estadual.md) - Validação de IE
- [Título Eleitoral](titulo_eleitoral.md) - Validação de Título Eleitoral
