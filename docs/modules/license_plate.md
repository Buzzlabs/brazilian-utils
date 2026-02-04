# Placa de Veículo - Validação e Conversão de Placas

Módulo para validação, detecção de formato e conversão de placas veiculares brasileiras.

## Visão Geral

Este módulo fornece funções para:

- Validar placas tradicionais
- Validar placas Mercosul (novo padrão)
- Detectar o formato de uma placa
- Converter placa tradicional para Mercosul
- Obter informações sobre erros de validação

## Formatos de Placa

### Placa Tradicional

- **7 caracteres** no total
- Formato: **LLL-NNNN** ou **LLLNNNN**
- Estrutura:
  - 3 letras (A-Z)
  - 4 números (0-9)
- Exemplo: ABC-1234 ou ABC1234
- Vigência: Até 2018 (ainda em circulação)

### Placa Mercosul

- **7 caracteres** no total
- Formato: **LLLNLNN** ou **LLL-N-L-NN**
- Estrutura:
  - 3 letras (A-Z)
  - 1 número (0-9)
  - 1 letra (A-Z) - identificador do estado
  - 2 números (0-9)
- Exemplo: ABC1D23
- Vigência: A partir de 2018 (novo padrão)

### Comparação

| Aspecto | Tradicional | Mercosul |
|---------|------------|----------|
| Formato | LLL-NNNN | LLLNLNN |
| Letras | 3 | 3 |
| Números | 4 | 3 (separados) |
| Total caracteres | 7 | 7 |
| Letra de estado | Não | Sim (3º posição após 1 número) |

## Mapeamento de Letras de Estado (Mercosul)

A letra central da placa Mercosul identifica o estado:

- A: SP (São Paulo)
- B: MG (Minas Gerais)
- C: RJ (Rio de Janeiro)
- D: BA (Bahia)
- E: PR (Paraná)
- F: RS (Rio Grande do Sul)
- G: SC (Santa Catarina)
- H: ES (Espírito Santo)
- I: GO (Goiás)
- J: PB (Paraíba)
- K: PA (Pará)
- L: CE (Ceará)
- M: PE (Pernambuco)
- N: MS (Mato Grosso do Sul)
- O: MT (Mato Grosso)
- P: DF (Distrito Federal)
- Q: AL (Alagoas)
- R: RN (Rio Grande do Norte)
- S: PI (Piauí)
- T: RO (Rondônia)
- U: AC (Acre)
- V: AM (Amazonas)
- W: RR (Roraima)
- X: AP (Amapá)
- Y: TO (Tocantins)
- Z: SE (Sergipe)

## Funções Principais

### is-valid?

```clojure
(plate/is-valid? plate-string)
```

Valida uma placa veicular (tradicional ou Mercosul).

**Argumentos:**
- `plate-string` (string): Placa a validar, com ou sem hífen

**Retorna:**
- `true` se válida, `false` caso contrário

**Exemplos:**
```clojure
(plate/is-valid? "ABC-1234")   ; => true (tradicional)
(plate/is-valid? "ABC1234")    ; => true (tradicional sem hífen)
(plate/is-valid? "ABC1D23")    ; => true (Mercosul)
(plate/is-valid? "ABC-1D23")   ; => true (Mercosul com hífen)
(plate/is-valid? "INVALID")    ; => false
(plate/is-valid? "123ABCD")    ; => false
```

### get-format

```clojure
(plate/get-format plate-string)
```

Detecta o formato de uma placa (tradicional ou Mercosul).

**Argumentos:**
- `plate-string` (string): Placa a analisar

**Retorna:**
- String descrevendo o formato:
  - `"LLLNNNN"` para placa tradicional
  - `"LLLNLNN"` para placa Mercosul
  - `nil` se placa inválida

**Exemplos:**
```clojure
(plate/get-format "ABC1234")   ; => "LLLNNNN" (tradicional)
(plate/get-format "ABC-1234")  ; => "LLLNNNN" (tradicional)
(plate/get-format "ABC1D23")   ; => "LLLNLNN" (Mercosul)
(plate/get-format "ABC-1D23")  ; => "LLLNLNN" (Mercosul)
(plate/get-format "INVALID")   ; => nil
```

### is-traditional?

```clojure
(plate/is-traditional? plate-string)
```

Verifica se a placa é do formato tradicional.

**Argumentos:**
- `plate-string` (string): Placa a verificar

**Retorna:**
- `true` se for tradicional, `false` caso contrário

**Exemplos:**
```clojure
(plate/is-traditional? "ABC1234")   ; => true
(plate/is-traditional? "ABC1D23")   ; => false
(plate/is-traditional? "INVALID")   ; => false
```

### is-mercosul?

```clojure
(plate/is-mercosul? plate-string)
```

Verifica se a placa é do formato Mercosul.

**Argumentos:**
- `plate-string` (string): Placa a verificar

**Retorna:**
- `true` se for Mercosul, `false` caso contrário

**Exemplos:**
```clojure
(plate/is-mercosul? "ABC1D23")   ; => true
(plate/is-mercosul? "ABC1234")   ; => false
(plate/is-mercosul? "INVALID")   ; => false
```

### convert-to-mercosul

```clojure
(plate/convert-to-mercosul traditional-plate)
```

Converte uma placa tradicional para o formato Mercosul.

**Argumentos:**
- `traditional-plate` (string): Placa tradicional a converter

**Retorna:**
- String com placa no formato Mercosul
- `nil` se placa não for válida ou já for Mercosul

**Exemplos:**
```clojure
(plate/convert-to-mercosul "ABC1234")   ; => "ABC1B34"
(plate/convert-to-mercosul "ABC-1234")  ; => "ABC1B34" (remove hífen)
(plate/convert-to-mercosul "ABC1D23")   ; => "ABC1D23" (já é Mercosul, retorna igual)
(plate/convert-to-mercosul "INVALID")   ; => nil
```

**Algoritmo de Conversão:**
- Posição 1-3: Mantém as 3 letras originais
- Posição 4: Letra do estado (baseado na placa original)
- Posição 5: Primeiro número original (mantém)
- Posição 6: Segundo número original (mantém)
- Posição 7: Terceiro número original (mantém)
- Posição 8: Quarto número original (mantém)

**Exemplo de conversão: ABC1234 → ABC1B34**
- AB**C** → AB**C** (letras iguais)
- 1234 → 1**B**34 (insere letra de estado na 2ª posição)

## Recursos

- ✅ Validação de placa tradicional
- ✅ Validação de placa Mercosul
- ✅ Detecção automática de formato
- ✅ Conversão de tradicional para Mercosul
- ✅ Suporte a múltiplos formatos (com e sem hífen)
- ✅ Mapeamento de letras de estado
- ✅ Cross-platform (Clojure & ClojureScript)

## Tratamento de Erros

```clojure
(plate/is-valid? "123ABCD")    ; => false (números na frente)
(plate/is-valid? "AB12345")    ; => false (formato inválido)
(plate/is-valid? "ABC")        ; => false (muito curto)
(plate/is-valid? "ABC1234XYZ") ; => false (muito longo)
```

## Casos de Uso

### Validar entrada de placa

```clojure
(defn validate-plate-input [plate-input]
  (if (plate/is-valid? plate-input)
    {:status :valid 
     :plate (clojure.string/upper-case plate-input)
     :format (plate/get-format plate-input)}
    {:status :invalid :message "Placa inválida"}))

(validate-plate-input "ABC1234")
; => {:status :valid :plate "ABC1234" :format "LLLNNNN"}

(validate-plate-input "abc1d23")
; => {:status :valid :plate "ABC1D23" :format "LLLNLNN"}
```

### Converter placa em lote

```clojure
(defn convert-traditional-to-mercosul [plate-list]
  (mapv (fn [plate]
          (if (plate/is-traditional? plate)
            {:original plate
             :converted (plate/convert-to-mercosul plate)
             :status :converted}
            {:original plate
             :converted plate
             :status :already-mercosul}))
        plate-list))

(convert-traditional-to-mercosul ["ABC1234" "XYZ5678" "DEF1G90"])
; => [{:original "ABC1234" :converted "ABC1B34" :status :converted} ...]
```

### Sistema de classificação de veículos

```clojure
(defn classify-vehicle [plate-string]
  (if-let [format (plate/get-format plate-string)]
    (case format
      "LLLNNNN" {:type :traditional :plate plate-string :status :valid}
      "LLLNLNN" {:type :mercosul :plate plate-string :status :valid}
      {:type :unknown :plate plate-string :status :invalid})
    {:type :unknown :plate plate-string :status :invalid}))

(classify-vehicle "ABC1234")   ; => {:type :traditional ...}
(classify-vehicle "ABC1D23")   ; => {:type :mercosul ...}
```

### Migração de sistema (tradicional para Mercosul)

```clojure
(defn migrate-plate-to-mercosul [vehicle-record]
  (let [converted (plate/convert-to-mercosul (:plate vehicle-record))]
    (if converted
      (assoc vehicle-record 
             :plate converted
             :plate-format :mercosul
             :migrated-at (java.time.LocalDateTime/now))
      vehicle-record)))

(migrate-plate-to-mercosul {:id 1 :plate "ABC1234" :owner "João"})
; => {:id 1 :plate "ABC1B34" :owner "João" :plate-format :mercosul :migrated-at ...}
```


## Ver Também

- [Estados](states.md) - Para informações sobre UFs (mapeamento com letras Mercosul)
