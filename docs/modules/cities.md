# Cidades - Lookup e Validação de Cidades Brasileiras

Módulo para busca e validação de cidades brasileiras por estado.

## Visão Geral

Este módulo fornece funções para:

- Listar cidades de um estado específico
- Buscar cidades pelo nome
- Validar existência de uma cidade em um estado
- Obter todas as cidades com informações de estado
- Verificar validade de estado antes de buscar cidades

## Estrutura de Dados de Cidade

### Formato

Uma cidade é representada como:
```clojure
{:state :SP          ; Código UF
 :city "São Paulo"}  ; Nome da cidade
```

### Base de Dados

A biblioteca contém a base completa de:
- **27 estados** (26 estados + DF)
- **5.565+ cidades** brasileiras
- Dados atualizados e validados

## Funções Principais

### cities-of

```clojure
(cities/cities-of uf-code)
```

Lista todas as cidades de um estado.

**Argumentos:**
- `uf-code` (keyword ou string): Código do estado (ex: :SP ou "SP")

**Retorna:**
- Vector com nomes das cidades em ordem alfabética
- Vector vazio `[]` se estado inválido

**Exemplos:**
```clojure
(cities/cities-of :SP)
; => ["Adamantina" "Adolfo" "Aguaí" "Águas de Lindóia" ...]

(cities/cities-of :RJ)
; => ["Acarí" "Acompanhamento" "Araruama" ...]

(cities/cities-of "MG")
; => ["Abaeté" "Abaíba" "Abaeté" ...]

(count (cities/cities-of :SP))  ; => 645 (número de cidades em SP)
```

**Notas:**
- Retorna vector vazio para UF inválido
- Cidades em ordem alfabética
- Nomes com acentuação preservada

### cities-of!

```clojure
(cities/cities-of! uf-code)
```

Lista cidades de um estado, lançando exceção se inválido.

**Argumentos:**
- `uf-code` (keyword ou string): Código do estado

**Retorna:**
- Vector com nomes das cidades

**Lança:**
- `ExceptionInfo` se estado inválido

**Exemplos:**
```clojure
(cities/cities-of! :SP)  ; => ["Adamantina" "Adolfo" ...]
(cities/cities-of! :XX)  ; Lança exception: "Invalid state: XX"
```

### find-city-by-name

```clojure
(cities/find-city-by-name city-name)
```

Busca cidades pelo nome (em qualquer estado).

**Argumentos:**
- `city-name` (string): Nome da cidade

**Retorna:**
- Vector com mapas `{:state :XX :city "Nome"}` para cada match
- Vector vazio `[]` se não encontrado

**Exemplos:**
```clojure
(cities/find-city-by-name "São Paulo")
; => [{:state :SP :city "São Paulo"}]

(cities/find-city-by-name "Brasília")
; => [{:state :DF :city "Brasília"}]

(cities/find-city-by-name "Santos")
; => [{:state :SP :city "Santos"}]

(cities/find-city-by-name "Não Existe")
; => []
```

**Notas:**
- Busca case-insensitive
- Retorna todas as ocorrências (se houver duplicatas de nomes)
- Inclui estado da cidade nos resultados

### city-exists?

```clojure
(cities/city-exists? uf-code city-name)
```

Verifica se uma cidade existe em um estado específico.

**Argumentos:**
- `uf-code` (keyword ou string): Código do estado
- `city-name` (string): Nome da cidade

**Retorna:**
- `true` se existir, `false` caso contrário

**Exemplos:**
```clojure
(cities/city-exists? :SP "São Paulo")     ; => true
(cities/city-exists? :SP "Campinas")      ; => true
(cities/city-exists? :SP "Rio de Janeiro") ; => false
(cities/city-exists? :RJ "Rio de Janeiro") ; => true
(cities/city-exists? :XX "Qualquer")      ; => false
```

### all-cities

```clojure
(cities/all-cities)
```

Lista todas as cidades de todos os estados.

**Retorna:**
- Vector com mapas `{:state :XX :city "Nome"}` para cada cidade
- Total de 5.565+ cidades

**Exemplos:**
```clojure
(count (cities/all-cities))  ; => 5565+

(take 3 (cities/all-cities))
; => [{:state :AC :city "Acrelândia"}
;     {:state :AC :city "Assis Brasil"}
;     {:state :AC :city "Brasileia"}]
```

### all-city-names

```clojure
(cities/all-city-names)
```

Lista os nomes de todas as cidades (sem informação de estado).

**Retorna:**
- Vector com nomes de cidades em ordem
- Total de 5.565+ cidades

**Exemplos:**
```clojure
(count (cities/all-city-names))  ; => 5565+

(take 5 (cities/all-city-names))
; => ["Acrelândia" "Assis Brasil" "Brasileia" "Capixaba" ...]
```

## Recursos

- ✅ Listagem de cidades por estado
- ✅ Busca de cidades por nome
- ✅ Validação de existência de cidade
- ✅ Base completa de cidades brasileiras
- ✅ Suporte a keywords e strings para UF
- ✅ Acentuação preservada
- ✅ Case-insensitive para buscas
- ✅ Cross-platform (Clojure & ClojureScript)

## Tratamento de Erros

```clojure
; Retorna vector vazio para estado inválido
(cities/cities-of :XX)       ; => []
(cities/city-exists? :XX "Qualquer")  ; => false

; Lança exceção (versão com !)
(cities/cities-of! :XX)      ; Lança exception

; Retorna vector vazio se não encontrado
(cities/find-city-by-name "Não Existe")  ; => []
```

## Casos de Uso

### Validar cidade do formulário

```clojure
(require '[brazilian-utils.cities :as cities])

(defn validar-cidade [estado cidade]
  (if (cities/city-exists? estado cidade)
    {:status :válido :estado estado :cidade cidade}
    {:status :inválido
     :mensagem (str "Cidade não encontrada em " estado)
     :sugestoes (cities/cities-of estado)}))

(validar-cidade :SP "São Paulo")
; => {:status :válido :estado :SP :cidade "São Paulo"}

(validar-cidade :SP "Curitiba")
; => {:status :inválido
;     :mensagem "Cidade não encontrada em SP"
;     :sugestoes ["Adamantina" "Adolfo" ...]}
```

### Autocomplete de cidades por estado

```clojure
(defn cidades-com-prefixo [estado prefixo]
  (->> (cities/cities-of estado)
       (filter #(.startsWith % prefixo))
       (take 10)))

(cidades-com-prefixo :SP "São")
; => ["São Bento do Sapucaí" "São Caetano do Sul" "São Carlos" ...]

(cidades-com-prefixo :RJ "Rio")
; => ["Rio Bonito" "Rio Claro" "Rio das Flores" "Rio das Ostras" "Rio de Janeiro"]
```

### Buscar estado de uma cidade

```clojure
(defn encontrar-estado [cidade]
  (->> (cities/find-city-by-name cidade)
       first
       :state))

(encontrar-estado "Brasília")      ; => :DF
(encontrar-estado "São Paulo")     ; => :SP
(encontrar-estado "Não Existe")    ; => nil
```

### Listar cidades com contagem

```clojure
(defn cidades-por-estado []
  (->> (cities/all-cities)
       (group-by :state)
       (map (fn [[state cities]]
              {:estado state
               :total (count cities)}))
       (sort-by :total >)))

(cidades-por-estado)
; => [{:estado :SP :total 645}
;     {:estado :MG :total 853}
;     {:estado :BA :total 417}
;     ...]
```

### Validar lista de cidades

```clojure
(defn validar-cidades-lote [estado cidade-list]
  (reduce (fn [acc cidade]
            (assoc acc cidade 
                   (cities/city-exists? estado cidade)))
          {}
          cidade-list))

(validar-cidades-lote :SP ["São Paulo" "Campinas" "Curitiba"])
; => {"São Paulo" true "Campinas" true "Curitiba" false}
```

## Estatísticas de Cidades

| Estado | Cidades |
|--------|---------|
| SP | 645 |
| MG | 853 |
| BA | 417 |
| RS | 497 |
| PR | 399 |
| PE | 184 |
| SC | 295 |
| GO | 246 |
| PA | 143 |
| MA | 217 |
| CE | 184 |
| RJ | 92 |
| **Total** | **5.565+** |


## Notas de Performance

Para aplicações com lookup frequente de cidades:

```clojure
; Bom: Cache para lookups repetidos
(def cities-by-state
  (->> (cities/all-cities)
       (group-by :state)))

(get-in cities-by-state [:SP])  ; Rápido (pre-computed)

; Bom: Validação em lote
(->> cidades-input
     (map (fn [c] [c (cities/city-exists? :SP c)]))
     (into {}))
```

## Ver Também

- [Estados](states.md) - Para validação e informações de estados
- [CEP](cep.md) - Para validação de endereços e busca por CEP

City data is loaded from `resources/cities.edn` at compile time and validated against the schema to ensure data integrity.
