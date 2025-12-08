# 💱 Monexa — Currency Converter (Android)

Um aplicativo Android desenvolvido para conversão de moedas fiduciárias, exibindo valores atualizados em tempo real (atualizados diariamente) e fornecendo ao usuário uma interface simples, intuitiva e organizada.

---

## 📌 Funcionalidades

- Consulta todas as **moedas fiduciárias disponíveis** para conversão.
- Seleção de moeda **de origem** e **de destino**.
- Campo para digitar o valor a ser convertido.
- Cálculo automático do valor final com base na cotação atual.
- Exibição clara do resultado:
  - Moeda de origem e moeda de destino.
  - Valor convertido.
  - **Última atualização** da cotação.
  - **Próxima atualização** programada.
- Atualizações diárias via API.
- Interface moderna e responsiva em XML.

---

## 🧱 Arquitetura

O projeto segue rigorosamente o padrão **Clean Architecture**, garantindo:

- **Separação de camadas:** Data → Domain → Presentation  
- **Alta testabilidade:** regras de negócio livres de dependências externas  
- **Escalabilidade facilitada:** mudanças isoladas por camada  
- **Manutenção simples:** contratos e abstrações bem definidos  

Diagrama geral (conceitual):

Presentation (Activity/Fragment + ViewBinding | ViewModel + MutableState)
↓
Domain (UseCases + Models + Repositories Interfaces)
↓
Data (Repository Implementations + Retrofit Services + Mappers + DTO)

---

## 🛠️ Tecnologias e Bibliotecas Utilizadas

### 🔹 **Linguagem**
- Kotlin

### 🔹 **UI**
- XML + ConstraintLayout  
- ViewBinding  
- DataBinding  

### 🔹 **Arquitetura**
- Clean Architecture  
- MVVM (ViewModel + estados via `MutableState`)  

### 🔹 **Injeção de Dependências**
- **Koin**

### 🔹 **Rede**
- **Retrofit** + Gson  
- Camada de Data separada em *DataSource*, *Repository* e *DTOs*

### 🔹 **Reatividade / Estados**
- `MutableState` para atualização de UI  
- Fluxo reativo simplificado na ViewModel

---

## 📡 Atualização das Cotações

- As cotações são atualizadas **uma vez ao dia** pela API.
- O app exibe:
  - **Última atualização** (formato brasileiro).
  - **Próxima atualização** prevista.
- O tratamento e formatação das datas são feitos na Presentation Layer.

---

## 📱 Layout

- Desenvolvido inteiramente em XML.
- Responsividade garantida com ConstraintLayout.
- Interface simples, minimalista e focada na clareza dos valores financeiros.

---

## 🧩 Estrutura de Pastas (resumo)

data
    /datasource
    /dto
    /mappers
    /repository
domain
    /model
    /repository
    /usecase
presentation
    /viewmodel
    /activity
di
  KoinModules.kt
  
  ---

## 🚀 Como Executar

1. Clone o repositório:
git clone https://github.com/kaueludovico/monexa-android.git
