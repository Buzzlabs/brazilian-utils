# Instalação

## Via Clojars

Brazilian Utils está disponível no [Clojars](https://clojars.org/br.com.buzzlabs/brazilian-utils).

### Clojure (deps.edn)

```clojure
{:deps {br.com.buzzlabs/brazilian-utils {:mvn/version "LATEST"}}}
```

### Leiningen (project.clj)

```clojure
:dependencies [[br.com.buzzlabs/brazilian-utils "LATEST"]]
```

### Gradle

```gradle
dependencies {
  implementation 'br.com.buzzlabs:brazilian-utils:LATEST'
}
```

## ClojureScript

A biblioteca suporta ClojureScript nativamente. Basta adicionar a dependência e usar com qualquer compilador:

- **Shadow-CLJS** - ✅ Suporte completo
- **Figwheel** - ✅ Suporte completo
- **CLJS CLI** - ✅ Suporte completo
- **Webpack/Shadow** - ✅ Suporte completo

## Node.js

```clojure
; Se usar shadow-cljs com Node.js target
npm install br.com.buzzlabs/brazilian-utils
```

Ou inclua via deps.edn e compile para Node.js.

## Verificar Instalação

Após instalar, verifique se está funcionando:

```clojure
; Clojure/ClojureScript
(require '[brazilian-utils.states :as states]
         '[brazilian-utils.cpf :as cpf])

; Teste de estado
(states/valid-uf? :SP)
; => true

; Teste de CPF
(cpf/is-valid? "123.456.789-09")
; => true/false
```

Se ambas as funções executarem sem erros, está tudo pronto!

## Plataformas Suportadas

| Plataforma | Versão | Status |
|-----------|--------|--------|
| Clojure | 1.9+ | ✅ Suportado |
| ClojureScript | 1.10+ | ✅ Suportado |
| Java | 8+ | ✅ Suportado |
| Node.js | 12+ | ✅ Suportado |

## Troubleshooting

### Erro: "Unable to resolve symbol"

Verifique que a dependência foi instalada corretamente e o namespace foi importado:

```clojure
(require '[brazilian-utils.states :as states])
```

### Suporte a ClojureScript com ViaCEP

Se usar integração com ViaCEP em ClojureScript, instale também:

```clojure
{:deps {cljs-http/cljs-http {:mvn/version "0.1.46"}}}
```
