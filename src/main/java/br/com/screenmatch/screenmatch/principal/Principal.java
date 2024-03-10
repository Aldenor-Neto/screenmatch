package br.com.screenmatch.screenmatch.principal;

import br.com.screenmatch.screenmatch.model.DadosEpisodio;
import br.com.screenmatch.screenmatch.model.DadosSerie;
import br.com.screenmatch.screenmatch.model.DadosTemporada;
import br.com.screenmatch.screenmatch.model.Episodio;
import br.com.screenmatch.screenmatch.service.ConsumoApi;
import br.com.screenmatch.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Principal {

        Scanner input = new Scanner(System.in);
        private ConverteDados converte = new ConverteDados();
        private ConsumoApi consumoApi = new ConsumoApi();
        private final String ENDERECO = "https://www.omdbapi.com/?t=";
        private final String API_KEY = "&apikey=6585022c";

        public void exibirMenu() {
                System.out.println("Infórme a série para busca!");
                var nomeSerie = input.nextLine();

                var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

                DadosSerie dadosSerie = converte.obterDados(json, DadosSerie.class);
                System.out.println(dadosSerie);

                List<DadosTemporada> temporadas = new ArrayList<>();
                for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
                        json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i
                                        + API_KEY);
                        DadosTemporada dadosTemporada = converte.obterDados(json,
                                        DadosTemporada.class);
                        temporadas.add(dadosTemporada);
                }
                // temporadas.forEach(System.out::println);
                // for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
                // List<DadosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
                // for (int j = 0; j < episodiosTemporadas.size(); j++) {
                // System.out.println(episodiosTemporadas.get(j).titulo());
                // }
                // }

                // temporadas.forEach(t -> t.episodios().forEach(e ->
                // System.out.println(e.titulo())));
                List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                                .flatMap(t -> t.episodios().stream())
                                .collect(Collectors.toList());

                /*
                 * System.out.println("\n Top 10 episodios");
                 * dadosEpisodios.stream()
                 * .filter(e -> !e.avaliacao().equalsIgnoreCase("n/a"))
                 * .peek(e -> System.out.println("filtro (n/a)" + e))
                 * .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                 * .peek(e -> System.out.println("ordenação " + e))
                 * .limit(10)
                 * .peek(e -> System.out.println("limite " + e))
                 * .map(e -> e.titulo().toUpperCase())
                 * .peek(e -> System.out.println("mapeamento " + e))
                 * .forEach(System.out::println);
                 * ;
                 */

                List<Episodio> episodios = temporadas.stream()
                                .flatMap(t -> t.episodios().stream()
                                                .map(d -> new Episodio(t.numero(), d)))
                                .collect(Collectors.toList());

                episodios.forEach(System.out::println);

                /*
                 * System.out.println("informe o título do episodio");
                 * var trechoTitulo = input.nextLine();
                 * 
                 * Optional<Episodio> episodioBuscado = episodios.stream()
                 * .filter(e ->
                 * e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                 * .findFirst();
                 * if (episodioBuscado.isPresent()) {
                 * System.out.println("episódio encontrado!");
                 * System.out.println(episodioBuscado);
                 * } else {
                 * System.out.println("episódio não encontrado!");
                 * }
                 */

                Map<Integer, Double> avaliacaoTemporadas = episodios.stream()
                                .filter(e -> e.getAvaliacao() > 0.0)
                                .collect(Collectors.groupingBy(Episodio::getTemporada,
                                                Collectors.averagingDouble(Episodio::getAvaliacao)));

                System.out.println(avaliacaoTemporadas);

                DoubleSummaryStatistics est = episodios.stream()
                                .filter(e -> e.getAvaliacao() > 0.0)
                                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

                                System.out.println(est);
                                System.out.println("Média "  + est.getAverage());
                                System.out.println("melhor episódio " + est.getMax());
                                System.out.println("pior eppisódio " + est.getMin());
                /*
                 * System.out.println("informe um ano para a busca");
                 * var ano = input.nextInt();
                 * input.nextLine();
                 * 
                 * LocalDate anobusca = LocalDate.of(ano, 1, 1);
                 * DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                 * 
                 * episodios.stream()
                 * .filter(e -> e.getDataLancamento() != null &&
                 * e.getDataLancamento().isAfter(anobusca))
                 * .forEach(e -> System.out.println("Temporada : " + e.getTemporada() +
                 * "Título : "
                 * + e.getTitulo() + "Avaliação : " + e.getAvaliacao()
                 * + "Data lançamento : " + e.getDataLancamento().format(dtf)));
                 */
        }

}
