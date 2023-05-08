<!--- Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com> -->

# Testing with compile-time Dependency Injection

If you're manually wiring up your application or using [[compile time dependency injection|ScalaCompileTimeDependencyInjection]] then you can directly use and customise your application components, or create a test variant specific to your test case. You can also modify and override filters, define routes, and specify configuration.

## BuiltInComponentsFromContext

[BuiltInComponentsFromContext](api/scala/play/api/BuiltInComponentsFromContext.html)  gives us an easy way to bootstrap your components. Given the context, this provides all required built in components: `environment`, `configuration`, `applicationLifecycle`, etc.

As described in [[compile time dependency injection|ScalaCompileTimeDependencyInjection]], this is the most common way of wiring up the application manually.

When testing, we can use the real components which allows us to start the complete application for full functional testing, or we can create a test components which starts a subset of the application as required.

## WithApplicationComponents

Key to testing the components is the [WithApplicationComponents](api/scala/org/scalatestplus/play/components/WithApplicationComponents.html) trait. This sets up the application, server and context ready for testing. There are a number of `sub-traits` available to mixin depending on your testing strategy
* [OneAppPerSuiteWithComponents](api/scala/org/scalatestplus/play/components/OneAppPerSuiteWithComponents.html)
* [OneAppPerTestWithComponents](api/scala/org/scalatestplus/play/components/OneAppPerTestWithComponents.html)
* [OneServerPerSuiteWithComponents](api/scala/org/scalatestplus/play/components/OneServerPerSuiteWithComponents.html)
* [OneServerPerTestWithComponents](api/scala/org/scalatestplus/play/components/OneServerPerTestWithComponents.html)

It is recommend to familiarise yourself with the documentation of each `trait` in order to decide which best fits your needs.

### Defining the components inline

As discussed, the components can be defined in line within the test. To do this, simply override the components and complete the implementation of the [BuiltInComponentsFromContext](api/scala/play/api/BuiltInComponentsFromContext.html) , providing the router.

@[scalacomponentstest-inlinecomponents](code/oneapppersuite/ExampleComponentsSpec.scala)

Above:
* We define the imports within the implementation to prevent conflicts between the `sird` and `play.api.http` packages when asserting verbs.
* We define a test router and implement the appropriate routes, in this case we match the root patch.
* We override the configuration to provide additional values to be used within the test, this is of course optional.

### Using existing components

If we want to use our existing application components, we can simply instantiate those within the test. 

@[scalacomponentstest-predefinedcomponents](code/oneapppertest/ExamplePreDefinedComponentsSpec.scala)

Additionally, it’s possible to override any definitions within the `components `at this stage, to  provide additional configuration or mock a database for example.

@[scalacomponentstest-predefinedcomponentsoverride](code/oneapppertest/ExamplePreDefinedOverrideComponentsSpec.scala)

## Complete Example

@[scalacomponentstest-oneapppersuite](code/oneapppersuite/ExampleComponentsSpec.scala)

## Nested Specs

If you have many tests that can share the same `Application`, and you don't want to put them all into one test class, you can place them into different `Suite` classes. These will be your nested suites. Create a master suite that extends the appropriate `trait`, for example`OneAppPerSuiteWithComponents`, and declares the nested `Suite`s. Finally, annotate the nested suites with `@DoNotDiscover` and have them extend `ConfiguredApp`. Here's an example:

@[scalacomponentstest-nestedsuites](code/oneapppersuite/NestedExampleComponentsSpec.scala)
