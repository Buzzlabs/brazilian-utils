![Logo do Brazilian Utils](https://github.com/brazilian-utils/brand/raw/main/github-hero/github-hero-js.png)

<div align="center">

<p>Utils library for Brazilian-specific businesses.</p>

<p><strong>Clojure</strong> • <strong>ClojureScript</strong></p>

[📖 Documentation](#installation)

[![Build Status](https://github.com/Buzzlabs/brazilian-utils/workflows/Tests/badge.svg)](https://github.com/Buzzlabs/brazilian-utils/actions?query=workflow%3ATests) [![Code Quality](https://github.com/Buzzlabs/brazilian-utils/workflows/Code%20Quality/badge.svg)](https://github.com/Buzzlabs/brazilian-utils/actions?query=workflow%3ACode%20Quality) [![Clojars](https://img.shields.io/clojars/v/com.github.Buzzlabs/brazilian-utils.svg)](https://clojars.org/com.github.Buzzlabs/brazilian-utils) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

</div>

**Table of Contents**

- [Installation](#installation)
  - [Clojure](#clojure)
  - [ClojureScript](#clojurescript)
- [Usage](#usage)
  - [Clojure](#clojure-1)
  - [ClojureScript](#clojurescript-1)
- [Contributors](#contributors)
- [License](#license)

# Installation

## Clojure

Add to your `deps.edn`:

```clojure
{:deps
 {com.github.Buzzlabs/brazilian-utils {:mvn/version "0.1.0"}}}
```

Or using Leiningen in your `project.clj`:

```clojure
:dependencies [[com.github.Buzzlabs/brazilian-utils "0.1.0"]]
```

## ClojureScript

Same as Clojure - add to your `deps.edn` or `project.clj` as shown above. Works with Shadow-CLJS, Figwheel, or any ClojureScript compiler.

# Usage

## Clojure

```clojure
(require '[brazilian-utils.states :as states]
         '[brazilian-utils.cities :as cities]
         '[brazilian-utils.cep :as cep])

(states/valid-uf? :SP)
(states/uf->state-name :SP)
(cities/cities-of :GO)
(cep/is-valid? "01310-100")
(cep/format-cep "01310100")
```

## ClojureScript

```clojure
(ns my-app
  (:require [brazilian-utils.states :as states]
            [brazilian-utils.cities :as cities]
            [brazilian-utils.cep :as cep]))

(states/valid-uf? :SP)
(states/uf->state-name :SP)
(cities/cities-of :GO)
(cep/is-valid? "01310-100")
(cep/format-cep "01310100")
```

### Setup

```bash
git clone https://github.com/Buzzlabs/brazilian-utils.git
cd brazilian-utils
```

### Run Tests

```bash
# Clojure
clojure -M:test

# ClojureScript
clojure -M:cljs
```

### Code Style

We use `cljfmt` for code formatting. Your PR should follow these style guidelines.

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/anabastos">
        <img src="https://avatars.githubusercontent.com/u/10088900?v=4" width="100px;" alt=""/>
        <br />
        <sub><b>Ana Bastos</b></sub>
      </a>
      <br />
      <a href="https://github.com/brazilian-utils/brazilian-utils/commits?author=anabastos" title="Code">💻</a>
    </td>
  </tr>
</table>

## License

[MIT](LICENSE)
