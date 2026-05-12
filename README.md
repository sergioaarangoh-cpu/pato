# OldPato 🦆

![Background](.github/screenshots/background_readme.png)

Un juego de disparos estilo feria desarrollado en Java con Swing, inspirado en el clásico juego de matar patos.
El pato más old de todos los patos llega a tu portátil éste 2026!

## Descripción

El jugador controla una mira en la pantalla y debe disparar a los patos que vuelan antes de que se acabe el tiempo o las vidas. Cada pato eliminado suma puntos y extiende el tiempo de juego.

## Screenshots

![Gameplay](.github/screenshots/background_readme.png)
![Mira](.github/screenshots/scope_readme.png)

## Mecánicas de juego

- **Vidas:** 5 vidas iniciales. Se pierde una vida cada vez que un pato escapa.
- **Puntuación:** +10 puntos por cada pato eliminado.
- **Tiempo:** 2 minutos base. Cada pato eliminado añade tiempo extra.
- **Power-ups:** objetos especiales que otorgan ventajas al jugador.

## Controles

| Acción | Control |
|---|---|
| Mover la mira | Mouse |
| Disparar | Clic izquierdo |
| Saltar instrucciones / Iniciar juego | Enter |
| Volver al menú | Escape |

> 🎮 Soporte para mando pendiente de implementar.

## Tecnologías

- Java Swing
- javax.sound.sampled
- Thread para movimiento de patos
- javax.swing.Timer para el contador de tiempo

## Arquitectura

MVC + principios SOLID (SRP (Single Responsability Principle) y DIP principalmente).

## Estado del proyecto

⚠️ En desarrollo.

## Autor

Sergio Arango — [@sergioaarangoh-cpu](https://github.com/sergioaarangoh-cpu)
Juan Sebastían Echeverri — [@juancho-2006](https://github.com/juancho-2006)
Victor Manuel Reyes — [@Victor13578](https://github.com/Victor13578)
