"""
Título: Práctica de Sistemas Distribuidos - Relojes Vectoriales y Cortes Consistentes
Versión: 4.0 (Formato Horizontal y Clean Code)
Autor: Andres Vera Cacho
Fecha: 23/05/2026
Resumen: Simulación de relojes vectoriales mediante algoritmo de Lamport. 
         Formato de salida matricial adaptado al enunciado y validación
         exhaustiva de cortes consistentes e inconsistentes.
"""

def _procesar_evento_local(accion, idx, nodo, t, relojes, cont_eventos, buffer_red, historial, eventos_en_t):
    if accion == '0' or accion.startswith('e-'):
        return

    cont_eventos[nodo] += 1
    id_evento = f"e{idx+1},{cont_eventos[nodo]}"
    relojes[nodo][idx] += 1
    
    if accion != 'e':
        buffer_red.setdefault(accion, []).append((list(relojes[nodo]), id_evento))
    
    historial[id_evento] = {'nodo': nodo, 't': t, 'reloj': list(relojes[nodo]), 'tipo': accion}
    eventos_en_t[nodo] = (id_evento, list(relojes[nodo]))


def _procesar_recepcion(accion, idx, nodo, t, relojes, cont_eventos, buffer_red, historial, eventos_en_t):
    if not accion.startswith('e-'):
        return

    cont_eventos[nodo] += 1
    id_evento = f"e{idx+1},{cont_eventos[nodo]}"
    relojes[nodo][idx] += 1
    
    id_msg = accion[2:]
    evt_origen = None
    if id_msg in buffer_red and buffer_red[id_msg]:
        reloj_msj, evt_origen = buffer_red[id_msg].pop(0)
        relojes[nodo] = [max(l, m) for l, m in zip(relojes[nodo], reloj_msj)]
    
    historial[id_evento] = {
        'nodo': nodo, 't': t, 'reloj': list(relojes[nodo]), 
        'tipo': accion, 'origen': evt_origen
    }
    eventos_en_t[nodo] = (id_evento, list(relojes[nodo]))


def simular_relojes(matriz, nodos, total_t):
    cont_eventos = dict.fromkeys(nodos, 0)
    relojes = {n: [0] * len(nodos) for n in nodos}
    tabla_salida = {n: [] for n in nodos}
    historial, buffer_red = {}, {}

    for t in range(total_t):
        eventos_en_t = {}
        for idx, nodo in enumerate(nodos):
            _procesar_evento_local(matriz[nodo][t], idx, nodo, t, relojes, cont_eventos, buffer_red, historial, eventos_en_t)

        for idx, nodo in enumerate(nodos):
            _procesar_recepcion(matriz[nodo][t], idx, nodo, t, relojes, cont_eventos, buffer_red, historial, eventos_en_t)

        # Preparar datos puros para luego pintarlos en horizontal
        for nodo in nodos:
            if nodo in eventos_en_t:
                ev, vec = eventos_en_t[nodo]
                tabla_salida[nodo].append({'t': t, 'ev': ev, 'vec': ",".join(map(str, vec))})
            else:
                vec_str = ",".join(["0"] * len(nodos)) if t == 0 else "*"
                tabla_salida[nodo].append({'t': t, 'ev': '*', 'vec': vec_str})

    return historial, tabla_salida


def calcular_relaciones(historial):
    def es_menor_estricto(v1, v2):
        return all(x <= y for x, y in zip(v1, v2)) and any(x < y for x, y in zip(v1, v2))

    eventos = list(historial.keys())
    causalidad, concurrencia = [], []

    for i in range(len(eventos)):
        for j in range(i + 1, len(eventos)):
            ev1, ev2 = eventos[i], eventos[j]
            r1, r2 = historial[ev1]['reloj'], historial[ev2]['reloj']

            if es_menor_estricto(r1, r2): causalidad.append(f"{ev1} -> {ev2}")
            elif es_menor_estricto(r2, r1): causalidad.append(f"{ev2} -> {ev1}")
            else: concurrencia.append(f"{ev1} || {ev2}")

    return causalidad, concurrencia


def verificar_corte_consistente(historial, limites_t, nombre_corte):
    corte = [e for e, data in historial.items() if data['t'] <= limites_t[data['nodo']]]
    
    for e in corte:
        data = historial[e]
        if 'origen' in data and data['origen'] not in corte:
            motivo = f"(El evento {e} de recepción está DENTRO, pero su origen {data['origen']} está FUERA)"
            print(f"{nombre_corte.ljust(45)} -> INCONSISTENTE {motivo}")
            return False

    print(f"{nombre_corte.ljust(45)} -> CONSISTENTE")
    return True


def imprimir_resultados(nodos, total_t, tabla_salida, causalidad, concurrencia, historial):
    print("\n" + "="*80)
    print("=== PARTE 1: TABLA DE ESTADOS GLOBAL ===")
    print("="*80 + "\n")
    
    # Construir cabecera T0, T1, T2...
    cabecera = "      " + "".join([f"T{t:<10}" for t in range(total_t)])
    separador = "-" * len(cabecera)
    print(cabecera)
    print(separador)
    
    # Imprimir en formato horizontal
    for nodo in nodos:
        print(f"{nodo}")
        linea_t = "      "
        linea_e = "      "
        linea_v = "      "
        
        for t in range(total_t):
            data = tabla_salida[nodo][t]
            linea_t += f"{data['t']:<11}"
            linea_e += f"{data['ev']:<11}"
            linea_v += f"{data['vec']:<11}"
            
        print(linea_t)
        print(linea_e)
        print(linea_v)
        print(separador)

    print("\n=== PARTE 2: RELACIONES CAUSALES Y CONCURRENTES ===")
    print(f"Ejemplos de Causalidad ({len(causalidad)} en total):")
    print(", ".join(causalidad[:10]) + " ...")
    
    print(f"\nEjemplos de Concurrencia ({len(concurrencia)} en total):")
    print(", ".join(concurrencia[:10]) + " ...")

    # Segundo ejercicio para subir 0,25
    print("\n=== PARTE 3: VALIDACIÓN DE CORTES ===")
    
    print("\n--- Cortes Simétricos (Columnas rectas, siempre consistentes en este flujo) ---")
    for t_actual in range(1, total_t):
        verificar_corte_consistente(historial, dict.fromkeys(nodos, t_actual), f"Corte recto en T={t_actual}")

    print("\n--- Cortes Asimétricos (Demostración del Algoritmo) ---")
    # Caso Consistente
    verificar_corte_consistente(historial, {'P1': 4, 'P2': 2, 'P3': 5}, "Corte (P1:4, P2:2, P3:5)")
    
    # Caso INCONSISTENTE: P1 recibe en T=2, pero congelamos P2 en T=0 (el mensaje aún no se ha enviado)
    verificar_corte_consistente(historial, {'P1': 2, 'P2': 0, 'P3': 2}, "Corte (P1:2, P2:0, P3:2)")


def main_distribuidos(matriz):
    nodos = list(matriz.keys())
    total_t = len(matriz[nodos[0]])
    
    historial, tabla_salida = simular_relojes(matriz, nodos, total_t)
    causalidad, concurrencia = calcular_relaciones(historial)
    imprimir_resultados(nodos, total_t, tabla_salida, causalidad, concurrencia, historial)


if __name__ == '__main__':
    # Es importante que la matriz tenga el formato correcto y que cada nodo tenga el mismo número de eventos (columnas)
    matriz_ejercicio = {
        'P1': ['0', 'i-1-3', 'e-i-2-1', '0', 'e-r-3-1', 'r-2-1', 'r-3-1', 'e'],
        'P2': ['0', 'i-2-1', '0',       '0', '0',       'e-i-3-2', 'e-r-2-1', '0'],
        'P3': ['0', 'e',     'e-i-1-3', 'r-3-1', 'i-3-2', 'e',     '0',       'e-r-3-1']
    }
    main_distribuidos(matriz_ejercicio)