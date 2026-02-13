(defproject com.github.Buzzlabs/brazilian-utils "0.1.0"
  :description "Utils library for Brazilian-specific businesses"
  :url "https://github.com/Buzzlabs/brazilian-utils"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.11.1"]]

  :plugins [[lein-codox "0.10.8"]]

  :codox {:output-path "doc/api"
          :source-paths ["src"]
          :doc-files ["README.MD"
                      "CHANGELOG.md"
                      "docs/index.md"
                      "docs/guides/usage.md"
                      "docs/guides/installation.md"
                      "docs/modules/boleto.md"
                      "docs/modules/capitalize.md"
                      "docs/modules/cep.md"
                      "docs/modules/cities.md"
                      "docs/modules/cnh.md"
                      "docs/modules/cnpj.md"
                      "docs/modules/cpf.md"
                      "docs/modules/currency.md"
                      "docs/modules/email.md"
                      "docs/modules/inscricao_estadual.md"
                      "docs/modules/license_plate.md"
                      "docs/modules/phone.md"
                      "docs/modules/pis.md"
                      "docs/modules/processo_juridico.md"
                      "docs/modules/renavam.md"
                      "docs/modules/states.md"
                      "docs/modules/titulo_eleitoral.md"]
          :source-uri "https://github.com/Buzzlabs/brazilian-utils/blob/{version}/{filepath}#L{line}"
          :metadata {:doc/format :markdown}}

  :deploy-repositories {"clojars" {:url "https://clojars.org/repo"
                                    :username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases false}})
